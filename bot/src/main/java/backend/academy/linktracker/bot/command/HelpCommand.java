package backend.academy.linktracker.bot.command;

import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements BotCommand {

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String handle(long chatId, String text) {
        return """
               Список доступных команд:
               /start — старт
               /help — список доступных команд
               """.strip();
    }
}
