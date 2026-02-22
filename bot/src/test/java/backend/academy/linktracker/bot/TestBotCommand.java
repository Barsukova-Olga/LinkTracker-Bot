package backend.academy.linktracker.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.academy.linktracker.bot.command.CommandDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestBotCommand {
    @Autowired
    CommandDispatcher dispatcher;

    @Test
    void startCommandWorks() {
        String reply = dispatcher.dispatch(1L, "/start");

        assertEquals(
                "Добро пожаловать в Barsukova LinkTracker Bot! Используйте /help, чтобы посмотреть доступные команды.",
                reply);
    }

    @Test
    void helpCommandWorks() {
        String reply = dispatcher.dispatch(1L, "/help");

        assertEquals("""
                Список доступных команд:
                /start — старт
                /help — список доступных команд
                """.strip(), reply);
    }

    @Test
    void unknownCommandWorks() {
        String reply = dispatcher.dispatch(1L, "/unknown");

        assertEquals("Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд.", reply);
    }
}
