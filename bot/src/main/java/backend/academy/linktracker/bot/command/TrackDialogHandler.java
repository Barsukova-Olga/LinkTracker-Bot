package backend.academy.linktracker.bot.command;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.AddLinkRequest;
import backend.academy.linktracker.bot.link.TrackedLink;
import backend.academy.linktracker.bot.link.parser.LinkParser;
import backend.academy.linktracker.bot.user.UserSession;
import backend.academy.linktracker.bot.user.UserSessionService;
import backend.academy.linktracker.bot.user.UserState;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class TrackDialogHandler {

    private final UserSessionService userSessionService;
    private final LinkParser linkParser;
    private final ScrapperClient scrapperClient;

    public TrackDialogHandler(
            UserSessionService userSessionService, LinkParser linkParser, ScrapperClient scrapperClient) {
        this.userSessionService = userSessionService;
        this.linkParser = linkParser;
        this.scrapperClient = scrapperClient;
    }

    public String handleMessage(long chatId, String text) {
        UserSession session = userSessionService
                .getSession(chatId)
                .orElseThrow(() -> new IllegalStateException("No active session for chatId=" + chatId));

        if (session.getState() == UserState.WAITING_LINK) {
            return handleLinkStep(chatId, text);
        }

        if (session.getState() == UserState.WAITING_TAG) {
            return handleTagsStep(chatId, text);
        }

        userSessionService.clearSession(chatId);
        return "Состояние диалога сброшено";
    }

    public String cancel(long chatId) {
        userSessionService.clearSession(chatId);
        return "Добавление ссылки отменено";
    }

    private String handleLinkStep(long chatId, String text) {
        Optional<TrackedLink> trackedLink = linkParser.parse(text);

        if (trackedLink.isEmpty()) {
            return "Ссылка некорректна. Поддерживаются GitHub и StackOverflow.";
        }

        TrackedLink parsedLink = trackedLink.orElseThrow();
        userSessionService.setLinkSession(chatId, parsedLink.getUrl());
        return "Введите теги через запятую или отправьте пустое сообщение, если теги не нужны.";
    }

    private String handleTagsStep(long chatId, String text) {
        UserSession session = userSessionService
                .getSession(chatId)
                .orElseThrow(() -> new IllegalStateException("No active session for chatId=" + chatId));

        List<String> tags = parseTags(text);

        try {
            scrapperClient.addLink(chatId, new AddLinkRequest(session.getUserLink(), tags, List.of()));

            userSessionService.clearSession(chatId);
            return "Ссылка сохранена";
        } catch (HttpClientErrorException e) {
            userSessionService.clearSession(chatId);

            if (e.getStatusCode().value() == 409) {
                return "Ссылка уже отслеживается";
            }

            throw e;
        }
    }

    private List<String> parseTags(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .toList();
    }
}
