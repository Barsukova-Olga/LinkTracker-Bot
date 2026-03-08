package backend.academy.linktracker.bot.command;

import backend.academy.linktracker.bot.user.UserSessionService;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessor {
    private final UserSessionService userSessionService;
    private final TrackDialogHandler trackDialogHandler;
    private final CommandDispatcher commandDispatcher;

    public MessageProcessor(
            UserSessionService userSessionService,
            TrackDialogHandler trackDialogHandler,
            CommandDispatcher commandDispatcher) {
        this.userSessionService = userSessionService;
        this.trackDialogHandler = trackDialogHandler;
        this.commandDispatcher = commandDispatcher;
    }

    public String process(long chatId, String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        if ("/cancel,".equals(text.trim())) {
            if (userSessionService.hasActiveSession(chatId)) {
                return trackDialogHandler.cancel(chatId);
            }
            return "Нет активных процессов";
        }

        if (userSessionService.hasActiveSession(chatId)) {
            if (text.trim().startsWith("/")) {
                userSessionService.clearSession(chatId);
                return commandDispatcher.dispatch(chatId, text);
            }
            return trackDialogHandler.handleMessage(chatId, text);
        }

        return commandDispatcher.dispatch(chatId, text);
    }
}
