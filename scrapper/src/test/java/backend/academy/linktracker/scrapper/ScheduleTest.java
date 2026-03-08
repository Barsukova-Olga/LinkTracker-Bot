package backend.academy.linktracker.scrapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import backend.academy.linktracker.scrapper.model.GithubParsedLink;
import backend.academy.linktracker.scrapper.model.TrackedParsedLink;
import backend.academy.linktracker.scrapper.repository.ScrapperRepository;
import backend.academy.linktracker.scrapper.schedule.LinkUpdateChecker;
import backend.academy.linktracker.scrapper.schedule.LinkUpdateNotifier;
import backend.academy.linktracker.scrapper.schedule.LinkUpdaterScheduler;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ScheduleTest {
    @Test
    void shouldNotifyOnlySubscribedChats() {
        ScrapperRepository scrapperRepository = mock(ScrapperRepository.class);
        LinkUpdateChecker linkUpdateChecker = mock(LinkUpdateChecker.class);
        LinkUpdateNotifier linkUpdateNotifier = mock(LinkUpdateNotifier.class);

        LinkUpdaterScheduler scheduler =
                new LinkUpdaterScheduler(scrapperRepository, linkUpdateNotifier, linkUpdateChecker);

        TrackedParsedLink link1 = new TrackedParsedLink(
                1L,
                new GithubParsedLink(URI.create("https://github.com/openai/openai-java"), "openai", "openai-java"),
                List.of("work"),
                List.of(),
                Instant.parse("2024-01-01T00:00:00Z"));

        TrackedParsedLink link2 = new TrackedParsedLink(
                2L,
                new GithubParsedLink(
                        URI.create("https://github.com/spring-projects/spring-boot"), "spring-projects", "spring-boot"),
                List.of("study"),
                List.of(),
                Instant.parse("2024-01-01T00:00:00Z"));

        when(scrapperRepository.findAll())
                .thenReturn(Map.of(
                        1L, List.of(link1),
                        2L, List.of(link2)));

        when(linkUpdateChecker.getCurrentLastUpdatedAt(link1))
                .thenReturn(Optional.of(Instant.parse("2024-01-02T00:00:00Z")));
        when(linkUpdateChecker.getCurrentLastUpdatedAt(link2))
                .thenReturn(Optional.of(Instant.parse("2024-01-02T00:00:00Z")));

        scheduler.update();

        verify(linkUpdateNotifier).notifyUpdate(1L, link1);
        verify(linkUpdateNotifier).notifyUpdate(2L, link2);
        verify(linkUpdateNotifier, never()).notifyUpdate(eq(999L), any());
    }
}
