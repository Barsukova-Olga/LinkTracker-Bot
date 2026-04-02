package backend.academy.linktracker.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.command.StartCommand;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class StartBotCommandTest {

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
    void startCommandShouldIgnoreConflict() {
        ScrapperClient scrapperClient = mock(ScrapperClient.class);

        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.CONFLICT);
        doThrow(ex).when(scrapperClient).registerChat(1L);

        StartCommand command = new StartCommand(scrapperClient);

        String result = command.handle(1L, "/start");

        assertEquals(
                "Добро пожаловать в Barsukova LinkTracker Bot! Используйте /help, чтобы посмотреть доступные команды.",
                result);
    }
}
