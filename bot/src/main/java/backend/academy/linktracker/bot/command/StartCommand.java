package backend.academy.linktracker.bot.command;

import backend.academy.linktracker.bot.client.ScrapperClient;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements BotCommand {

    private final ScrapperClient scrapperClient;

    public StartCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String handle(long chatId, String text) {
        scrapperClient.registerChat(chatId);
        return "Добро пожаловать в Barsukova LinkTracker Bot! Используйте /help, чтобы посмотреть доступные команды.";
    }
}
