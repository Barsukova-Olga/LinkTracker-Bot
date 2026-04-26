package backend.academy.linktracker.scrapper.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import backend.academy.linktracker.scrapper.client.github.GithubClient;
import backend.academy.linktracker.scrapper.client.github.dto.GithubIssueResponse;
import backend.academy.linktracker.scrapper.client.github.dto.GithubUserResponse;
import backend.academy.linktracker.scrapper.client.stackoverflow.StackoverflowClient;
import backend.academy.linktracker.scrapper.configuration.properties.ScheduleProperties;
import backend.academy.linktracker.scrapper.link.parser.LinkParser;
import backend.academy.linktracker.scrapper.model.GithubParsedLink;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.ChatLinkRepository;
import backend.academy.linktracker.scrapper.repository.LinkRepository;
import backend.academy.linktracker.scrapper.service.updater.DefaultLinkUpdateProcessor;
import backend.academy.linktracker.scrapper.service.updater.DefaultLinkUpdaterService;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdateProcessor;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdateReport;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdateTransactionService;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class DefaultLinkUpdaterServiceTest {

    private LinkRepository linkRepository;
    private ChatLinkRepository chatLinkRepository;
    private LinkUpdateProcessor linkUpdateProcessor;
    private LinkUpdateTransactionService linkUpdateTransactionService;

    private ScheduleProperties scheduleProperties;
    private DefaultLinkUpdaterService service;

    @BeforeEach
    void setUp() {
        linkRepository = mock(LinkRepository.class);
        chatLinkRepository = mock(ChatLinkRepository.class);
        linkUpdateProcessor = mock(LinkUpdateProcessor.class);
        linkUpdateTransactionService = mock(LinkUpdateTransactionService.class);
        scheduleProperties = mock(ScheduleProperties.class);

        service = new DefaultLinkUpdaterService(
                linkRepository,
                chatLinkRepository,
                linkUpdateProcessor,
                linkUpdateTransactionService,
                scheduleProperties);
    }

    @Test
    void shouldBuildGithubIssueMessageWithTitleAuthorAndPreview() {
        when(scheduleProperties.getBatchSize()).thenReturn(50);
        when(scheduleProperties.getThreads()).thenReturn(1);

        Link link = new Link(1L, "https://github.com/openai/openai-java", null, Instant.now());

        when(linkRepository.findBatch(50, 0)).thenReturn(List.of(link));
        when(linkRepository.findBatch(50, 50)).thenReturn(List.of());

        when(linkUpdateProcessor.process(link))
                .thenReturn(Optional.of(new LinkUpdateEvent(
                        1L,
                        URI.create(link.url()),
                        Instant.parse("2026-01-01T10:00:00Z"),
                        "New issue title",
                        "alice",
                        "Issue preview text")));

        when(chatLinkRepository.findChatsByLinkId(1L)).thenReturn(List.of(101L));

        service.update();

        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);

        verify(linkUpdateTransactionService)
                .saveUpdateAndOutbox(eq(link), any(LinkUpdateEvent.class), descriptionCaptor.capture());

        String description = descriptionCaptor.getValue();

        assertTrue(description.contains("New issue title"));
        assertTrue(description.contains("alice"));
        assertTrue(description.contains("Issue preview text"));
    }

    @Test
    void shouldBuildStackoverflowAnswerMessageWithTitleAuthorAndPreview() {
        when(scheduleProperties.getBatchSize()).thenReturn(50);
        when(scheduleProperties.getThreads()).thenReturn(1);

        Link link = new Link(2L, "https://stackoverflow.com/questions/11227809", null, Instant.now());

        when(linkRepository.findBatch(50, 0)).thenReturn(List.of(link));
        when(linkRepository.findBatch(50, 50)).thenReturn(List.of());

        when(linkUpdateProcessor.process(link))
                .thenReturn(Optional.of(new LinkUpdateEvent(
                        2L,
                        URI.create(link.url()),
                        Instant.parse("2026-01-02T12:30:00Z"),
                        "How to reverse a string in Java?",
                        "Bob Smith",
                        "You can use StringBuilder and call reverse().")));

        when(chatLinkRepository.findChatsByLinkId(2L)).thenReturn(List.of(202L));

        service.update();

        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);

        verify(linkUpdateTransactionService)
                .saveUpdateAndOutbox(eq(link), any(LinkUpdateEvent.class), descriptionCaptor.capture());

        String description = descriptionCaptor.getValue();

        assertTrue(description.contains("How to reverse a string in Java?"));
        assertTrue(description.contains("Bob Smith"));
        assertTrue(description.contains("You can use StringBuilder and call reverse()."));
    }

    @Test
    void shouldHandleUnavailableExternalApiAndContinueProcessingOtherLinks() {
        when(scheduleProperties.getBatchSize()).thenReturn(2);
        when(scheduleProperties.getThreads()).thenReturn(1);

        Link failedLink = new Link(1L, "https://github.com/openai/openai-java", null, Instant.now());

        Link okLink = new Link(2L, "https://stackoverflow.com/questions/11227809", null, Instant.now());

        LinkUpdateEvent okEvent = new LinkUpdateEvent(
                2L,
                URI.create(okLink.url()),
                Instant.parse("2026-01-02T12:30:00Z"),
                "How to reverse a string in Java?",
                "Bob Smith",
                "You can use StringBuilder and call reverse().");

        when(linkRepository.findBatch(2, 0)).thenReturn(List.of(failedLink, okLink));
        when(linkRepository.findBatch(2, 2)).thenReturn(List.of());

        when(linkUpdateProcessor.process(failedLink)).thenThrow(new RuntimeException("External API unavailable"));

        when(linkUpdateProcessor.process(okLink)).thenReturn(Optional.of(okEvent));
        when(chatLinkRepository.findChatsByLinkId(okLink.id())).thenReturn(List.of(123L));

        when(linkUpdateTransactionService.saveUpdateAndOutbox(eq(okLink), eq(okEvent), anyString()))
                .thenReturn(true);

        LinkUpdateReport report = service.update();

        assertEquals(2, report.totalProcessed());
        assertEquals(1, report.totalUpdated());
        assertEquals(List.of("https://github.com/openai/openai-java"), report.failedLinks());

        verify(linkUpdateProcessor).process(failedLink);
        verify(linkUpdateProcessor).process(okLink);

        verify(linkUpdateTransactionService, times(1)).saveUpdateAndOutbox(eq(okLink), eq(okEvent), anyString());

        verify(linkUpdateTransactionService, never())
                .saveUpdateAndOutbox(eq(failedLink), any(LinkUpdateEvent.class), anyString());
    }

    @Test
    void shouldTrimGithubPreviewTo200Characters() {
        GithubClient githubClient = mock(GithubClient.class);
        StackoverflowClient stackoverflowClient = mock(StackoverflowClient.class);
        LinkParser linkParser = mock(LinkParser.class);

        DefaultLinkUpdateProcessor processor =
                new DefaultLinkUpdateProcessor(githubClient, stackoverflowClient, linkParser);

        Link link = new Link(1L, "https://github.com/openai/openai-java", null, Instant.now());

        GithubParsedLink parsedLink = new GithubParsedLink(URI.create(link.url()), "openai", "openai-java");

        String longBody = "a".repeat(250);

        when(linkParser.parse(link.url())).thenReturn(Optional.of(parsedLink));
        when(githubClient.getIssues(parsedLink)).thenReturn(new GithubIssueResponse[] {
            new GithubIssueResponse(
                    1L,
                    "Issue title",
                    longBody,
                    new GithubUserResponse("alice"),
                    Instant.parse("2026-01-01T10:00:00Z"),
                    null)
        });

        Optional<LinkUpdateEvent> result = processor.process(link);

        assertTrue(result.isPresent());
        assertEquals(200, result.get().preview().length());
        assertEquals("a".repeat(200), result.get().preview());
    }

    @Test
    void shouldUseConfiguredBatchSizeAndProcessMultipleBatches() {
        when(scheduleProperties.getBatchSize()).thenReturn(2);
        when(scheduleProperties.getThreads()).thenReturn(1);

        Link link1 = new Link(1L, "https://github.com/a/1", null, Instant.now());
        Link link2 = new Link(2L, "https://github.com/a/2", null, Instant.now());
        Link link3 = new Link(3L, "https://github.com/a/3", null, Instant.now());
        Link link4 = new Link(4L, "https://github.com/a/4", null, Instant.now());
        Link link5 = new Link(5L, "https://github.com/a/5", null, Instant.now());

        when(linkRepository.findBatch(2, 0)).thenReturn(List.of(link1, link2));
        when(linkRepository.findBatch(2, 2)).thenReturn(List.of(link3, link4));
        when(linkRepository.findBatch(2, 4)).thenReturn(List.of(link5));
        when(linkRepository.findBatch(2, 6)).thenReturn(List.of());

        when(linkUpdateProcessor.process(any(Link.class))).thenReturn(Optional.empty());

        service.update();

        verify(linkRepository).findBatch(2, 0);
        verify(linkRepository).findBatch(2, 2);
        verify(linkRepository).findBatch(2, 4);
        verify(linkRepository).findBatch(2, 6);

        verify(linkUpdateProcessor).process(link1);
        verify(linkUpdateProcessor).process(link2);
        verify(linkUpdateProcessor).process(link3);
        verify(linkUpdateProcessor).process(link4);
        verify(linkUpdateProcessor).process(link5);
    }

    @Test
    void shouldReturnReportWithFailedLinksAndCounters() {
        when(scheduleProperties.getBatchSize()).thenReturn(3);
        when(scheduleProperties.getThreads()).thenReturn(1);

        Link link1 = new Link(1L, "https://github.com/a/b", null, Instant.now());
        Link link2 = new Link(2L, "https://github.com/c/d", null, Instant.now());
        Link link3 = new Link(3L, "https://stackoverflow.com/questions/123", null, Instant.now());

        when(linkRepository.findBatch(3, 0)).thenReturn(List.of(link1, link2, link3));
        when(linkRepository.findBatch(3, 3)).thenReturn(List.of());

        when(linkUpdateProcessor.process(link1)).thenThrow(new RuntimeException("GitHub API failed"));

        when(linkUpdateProcessor.process(link2))
                .thenReturn(Optional.of(new LinkUpdateEvent(
                        2L,
                        URI.create(link2.url()),
                        Instant.parse("2026-01-01T11:00:00Z"),
                        "Issue 2",
                        "bob",
                        "preview 2")));

        when(linkUpdateProcessor.process(link3)).thenReturn(Optional.empty());

        when(chatLinkRepository.findChatsByLinkId(link2.id())).thenReturn(List.of(42L));

        when(linkUpdateTransactionService.saveUpdateAndOutbox(any(Link.class), any(LinkUpdateEvent.class), anyString()))
                .thenReturn(true);

        LinkUpdateReport report = service.update();

        assertEquals(3, report.totalProcessed());
        assertEquals(1, report.totalUpdated());
        assertIterableEquals(List.of("https://github.com/a/b"), report.failedLinks());

        verify(linkUpdateTransactionService, times(1))
                .saveUpdateAndOutbox(eq(link2), any(LinkUpdateEvent.class), anyString());
    }

    @Test
    void shouldProcessLinksInParallel() throws Exception {
        when(scheduleProperties.getBatchSize()).thenReturn(4);
        when(scheduleProperties.getThreads()).thenReturn(2);

        Link link1 = new Link(1L, "https://github.com/a/1", null, Instant.now());
        Link link2 = new Link(2L, "https://github.com/a/2", null, Instant.now());
        Link link3 = new Link(3L, "https://github.com/a/3", null, Instant.now());
        Link link4 = new Link(4L, "https://github.com/a/4", null, Instant.now());

        when(linkRepository.findBatch(4, 0)).thenReturn(List.of(link1, link2, link3, link4));
        when(linkRepository.findBatch(4, 4)).thenReturn(List.of());

        when(chatLinkRepository.findChatsByLinkId(1L)).thenReturn(List.of(101L));
        when(chatLinkRepository.findChatsByLinkId(2L)).thenReturn(List.of(102L));
        when(chatLinkRepository.findChatsByLinkId(3L)).thenReturn(List.of(103L));
        when(chatLinkRepository.findChatsByLinkId(4L)).thenReturn(List.of(104L));

        when(linkUpdateTransactionService.saveUpdateAndOutbox(any(Link.class), any(LinkUpdateEvent.class), anyString()))
                .thenReturn(true);

        CountDownLatch started = new CountDownLatch(2);
        CountDownLatch release = new CountDownLatch(1);

        when(linkUpdateProcessor.process(any(Link.class))).thenAnswer(invocation -> {
            Link link = invocation.getArgument(0);
            started.countDown();
            release.await(2, TimeUnit.SECONDS);

            return Optional.of(new LinkUpdateEvent(
                    link.id(),
                    URI.create(link.url()),
                    Instant.parse("2026-01-01T10:00:00Z"),
                    "title-" + link.id(),
                    "author-" + link.id(),
                    "preview-" + link.id()));
        });

        ExecutorService testExecutor = Executors.newSingleThreadExecutor();
        Future<LinkUpdateReport> future = testExecutor.submit(service::update);

        assertTrue(started.await(2, TimeUnit.SECONDS));
        release.countDown();

        LinkUpdateReport report = future.get(3, TimeUnit.SECONDS);
        testExecutor.shutdownNow();

        assertEquals(4, report.totalProcessed());
        assertEquals(4, report.totalUpdated());
        assertEquals(List.of(), report.failedLinks());

        verify(linkRepository).findBatch(4, 0);
        verify(linkRepository).findBatch(4, 4);
        verify(linkUpdateProcessor, times(4)).process(any(Link.class));
        verify(linkUpdateTransactionService, times(4))
                .saveUpdateAndOutbox(any(Link.class), any(LinkUpdateEvent.class), anyString());
    }
}
