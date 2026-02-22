package backend.academy.linktracker.bot;

import backend.academy.linktracker.bot.command.CommandDispatcher;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TelegramPollingService {
    private static final Logger log = LoggerFactory.getLogger(TelegramPollingService.class);

    private final TelegramBot bot;
    private final CommandDispatcher dispatcher;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private volatile int offset = 0;

    public TelegramPollingService(TelegramBot bot, CommandDispatcher dispatcher) {
        this.bot = bot;
        this.dispatcher = dispatcher;
    }

    @PostConstruct
    public void start() {
        executor.scheduleWithFixedDelay(this::pollSafely, 0, 1, TimeUnit.SECONDS);
        log.info("telegram polling started");
    }

    private void pollSafely() {
        try {
            poll();
        } catch (Exception e) {
            log.warn("telegram polling failed", e);
        }
    }

    private void poll() {
        var req = new GetUpdates().offset(offset).timeout((int)
                Duration.ofSeconds(20).toSeconds());
        var resp = bot.execute(req);

        if (!resp.isOk()) {
            log.warn("telegram.getUpdates.failed code={} desc={}", resp.errorCode(), resp.description());
            return;
        }

        List<Update> upd = resp.updates();
        if (upd == null || upd.isEmpty()) return;

        for (Update u : upd) {
            offset = Math.max(offset, u.updateId() + 1);

            if (u.message() == null || u.message().text() == null) continue;

            long chatId = u.message().chat().id();
            String text = u.message().text();

            String string = dispatcher.dispatch(chatId, text);

            bot.execute(new SendMessage(chatId, string));
        }
    }
}
