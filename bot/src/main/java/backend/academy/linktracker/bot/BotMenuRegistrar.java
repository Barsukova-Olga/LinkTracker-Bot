package backend.academy.linktracker.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class BotMenuRegistrar {

    private final TelegramBot bot;

    public BotMenuRegistrar(TelegramBot bot) {
        this.bot = bot;
    }

    @PostConstruct
    public void registerCommands() {
        bot.execute(new SetMyCommands(
                new BotCommand("/start", "начать работу"),
                new BotCommand("/help", "список команд"),
                new BotCommand("/track", "отслеживание ссылок"),
                new BotCommand("/untrack", "отмена отслеживание"),
                new BotCommand("/list", "список всех ссылок, отслеживаемых пользователем")));
    }
}
