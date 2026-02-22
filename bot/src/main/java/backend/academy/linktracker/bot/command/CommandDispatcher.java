package backend.academy.linktracker.bot.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CommandDispatcher {

    private final Map<String, BotCommand> handlersByCommand;
    private final UnknownCommand unknownCommand;

    public CommandDispatcher(List<BotCommand> handlers, UnknownCommand unknownCommand) {
        this.unknownCommand = unknownCommand;
        this.handlersByCommand = new HashMap<>();

        for (BotCommand handler : handlers) {
            String cmd = handler.command().trim().toLowerCase();
            handlersByCommand.put(cmd, handler);
        }
    }

    public String dispatch(long chatId, String messageText) {
        String cmd = extractCommand(messageText);
        BotCommand handler = handlersByCommand.get(cmd);
        if (handler == null) {
            return unknownCommand.handle(cmd);
        }
        return handler.handle(chatId, messageText);
    }

    private static String extractCommand(String messageText) {
        if (messageText == null) return "";
        String trimmed = messageText.trim();
        if (!trimmed.startsWith("/")) return "";

        int space = trimmed.indexOf(' ');
        String firstToken = (space == -1) ? trimmed : trimmed.substring(0, space);

        return firstToken.trim().toLowerCase();
    }
}
