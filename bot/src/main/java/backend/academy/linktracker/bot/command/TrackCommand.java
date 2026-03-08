package backend.academy.linktracker.bot.command;

import backend.academy.linktracker.bot.user.UserSessionService;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand implements BotCommand {

    private final UserSessionService userSessionService;

    public TrackCommand(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public String handle(long chatId, String text) {
        userSessionService.startSession(chatId);
        return "Отправьте ссылку для отслеживания.\nДля отмены введите /cancel";
    }
}
