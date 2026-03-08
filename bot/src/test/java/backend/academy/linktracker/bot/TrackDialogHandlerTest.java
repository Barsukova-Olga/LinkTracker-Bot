package backend.academy.linktracker.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.command.TrackDialogHandler;
import backend.academy.linktracker.bot.dto.AddLinkRequest;
import backend.academy.linktracker.bot.link.LinkType;
import backend.academy.linktracker.bot.link.TrackedLink;
import backend.academy.linktracker.bot.link.parser.LinkParser;
import backend.academy.linktracker.bot.user.UserSession;
import backend.academy.linktracker.bot.user.UserSessionService;
import backend.academy.linktracker.bot.user.UserState;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

class TrackDialogHandlerTest {

    //    @Test
    void shouldAcceptCorrectLinkAndAskForTags() {
        UserSessionService userSessionService = mock(UserSessionService.class);
        LinkParser linkParser = mock(LinkParser.class);
        ScrapperClient scrapperClient = mock(ScrapperClient.class);

        TrackDialogHandler handler = new TrackDialogHandler(userSessionService, linkParser, scrapperClient);

        long chatId = 1L;
        URI uri = URI.create("https://github.com/openai/openai-java");

        when(userSessionService.getSession(chatId))
                .thenReturn(Optional.of(new UserSession(chatId, UserState.WAITING_LINK, null)));

        when(linkParser.parse("https://github.com/openai/openai-java"))
                .thenReturn(Optional.of(new TrackedLink(LinkType.GITHUB, uri)));

        String result = handler.handleMessage(chatId, "https://github.com/openai/openai-java");

        assertEquals("Введите теги через запятую или отправьте пустое сообщение, если теги не нужны.", result);
        verify(userSessionService).setLinkSession(chatId, uri);
    }

    //    @Test
    void shouldSaveLinkWhenTagsAreProvided() {
        UserSessionService userSessionService = mock(UserSessionService.class);
        LinkParser linkParser = mock(LinkParser.class);
        ScrapperClient scrapperClient = mock(ScrapperClient.class);

        TrackDialogHandler handler = new TrackDialogHandler(userSessionService, linkParser, scrapperClient);

        long chatId = 1L;
        URI uri = URI.create("https://github.com/openai/openai-java");

        when(userSessionService.getSession(chatId))
                .thenReturn(Optional.of(new UserSession(chatId, UserState.WAITING_TAG, uri)));

        String result = handler.handleMessage(chatId, "work, backend");

        assertEquals("Ссылка сохранена", result);
        verify(scrapperClient).addLink(eq(chatId), eq(new AddLinkRequest(uri, List.of("work", "backend"), List.of())));
        verify(userSessionService).clearSession(chatId);
    }

    //    @Test
    void shouldRejectInvalidLink() {
        UserSessionService userSessionService = mock(UserSessionService.class);
        LinkParser linkParser = mock(LinkParser.class);
        ScrapperClient scrapperClient = mock(ScrapperClient.class);

        TrackDialogHandler handler = new TrackDialogHandler(userSessionService, linkParser, scrapperClient);

        long chatId = 1L;

        when(userSessionService.getSession(chatId))
                .thenReturn(Optional.of(new UserSession(chatId, UserState.WAITING_LINK, null)));

        when(linkParser.parse("tbank://github.com/user/repo")).thenReturn(Optional.empty());

        String result = handler.handleMessage(chatId, "tbank://github.com/user/repo");

        assertEquals("Ссылка некорректна. Поддерживаются GitHub и StackOverflow.", result);

        verify(userSessionService, never()).setLinkSession(anyLong(), any());
        verifyNoInteractions(scrapperClient);
    }

    //    @Test
    void shouldReturnAlreadyTrackedMessageWhenScrapperReturnsConflict() {
        UserSessionService userSessionService = mock(UserSessionService.class);
        LinkParser linkParser = mock(LinkParser.class);
        ScrapperClient scrapperClient = mock(ScrapperClient.class);

        TrackDialogHandler handler = new TrackDialogHandler(userSessionService, linkParser, scrapperClient);

        long chatId = 1L;
        URI uri = URI.create("https://github.com/openai/openai-java");

        when(userSessionService.getSession(chatId))
                .thenReturn(Optional.of(new UserSession(chatId, UserState.WAITING_TAG, uri)));

        doThrow(new HttpClientErrorException(HttpStatus.CONFLICT))
                .when(scrapperClient)
                .addLink(eq(chatId), any(AddLinkRequest.class));

        String result = handler.handleMessage(chatId, "work, backend");

        assertEquals("Ссылка уже отслеживается", result);
    }
}
