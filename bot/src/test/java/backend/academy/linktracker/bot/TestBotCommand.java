package backend.academy.linktracker.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.command.BotCommand;
import backend.academy.linktracker.bot.command.CommandDispatcher;
import backend.academy.linktracker.bot.command.HelpCommand;
import backend.academy.linktracker.bot.command.StartCommand;
import backend.academy.linktracker.bot.command.UnknownCommand;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestBotCommand {

    @Test
    void startCommandWorks() {
        ScrapperClient scrapperClient = mock(ScrapperClient.class);
        doNothing().when(scrapperClient).registerChat(1L);

        StartCommand startCommand = new StartCommand(scrapperClient);

        String reply = startCommand.handle(1L, "/start");

        assertEquals(
                "Добро пожаловать в Barsukova LinkTracker Bot! Используйте /help, чтобы посмотреть доступные команды.",
                reply);
    }

    @Test
    void helpCommandWorks() {
        UnknownCommand unknownCommand = new UnknownCommand();
        BotCommand helpCommand = new HelpCommand();

        CommandDispatcher dispatcher = new CommandDispatcher(List.of(helpCommand), unknownCommand);

        String reply = dispatcher.dispatch(1L, "/help");

        assertEquals("""
                Список доступных команд:
                /start — старт
                /help — список доступных команд
                """.strip(), reply);
    }

    @Test
    void unknownCommandWorks() {
        UnknownCommand unknownCommand = new UnknownCommand();
        BotCommand helpCommand = new HelpCommand();

        CommandDispatcher dispatcher = new CommandDispatcher(List.of(helpCommand), unknownCommand);

        String reply = dispatcher.dispatch(1L, "/unknown");

        assertEquals("Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд.", reply);
    }
}
