package backend.academy.linktracker.bot.command;

import org.springframework.stereotype.Component;

@Component
public class StartCommand implements BotCommand {

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String handle(long chatId, String text) {
        return "Добро пожаловать в Barsukova LinkTracker Bot! Используйте /help, чтобы посмотреть доступные команды.";
    }
}
