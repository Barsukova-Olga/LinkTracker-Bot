package backend.academy.linktracker.bot.command;

import backend.academy.linktracker.bot.client.ScrapperClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
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
        try {
            scrapperClient.registerChat(chatId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() != 409) {
                throw e;
            }
            log.info("Chat {} is already registered", chatId);
        }

        return "Добро пожаловать в Barsukova LinkTracker Bot! Используйте /help, чтобы посмотреть доступные команды.";
    }
}
