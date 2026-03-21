package backend.academy.linktracker.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.command.ListCommand;
import backend.academy.linktracker.bot.dto.LinkResponse;
import backend.academy.linktracker.bot.dto.ListLinksResponse;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ListCommandTest {

    @Test
    void shouldReturnTrackedLinksList() {
        ScrapperClient scrapperClient = mock(ScrapperClient.class);
        ListCommand listCommand = new ListCommand(scrapperClient);

        long chatId = 1L;

        ListLinksResponse response = new ListLinksResponse(
                List.of(
                        new LinkResponse(
                                1L, URI.create("https://github.com/openai/openai-java"), List.of("work"), List.of()),
                        new LinkResponse(
                                2L,
                                URI.create("https://stackoverflow.com/questions/11227809/test"),
                                List.of("study"),
                                List.of())),
                2);

        when(scrapperClient.getLinks(chatId, null)).thenReturn(response);

        String result = listCommand.handle(chatId, "/list");

        assertEquals(
                "Отслеживаемые ссылки:\n"
                        + "- https://github.com/openai/openai-java\n"
                        + "- https://stackoverflow.com/questions/11227809/test\n",
                result);
    }

    @Test
    void shouldReturnEmptyMessageWhenNoTrackedLinks() {
        ScrapperClient scrapperClient = mock(ScrapperClient.class);
        ListCommand listCommand = new ListCommand(scrapperClient);

        long chatId = 1L;

        when(scrapperClient.getLinks(chatId, null)).thenReturn(new ListLinksResponse(List.of(), 0));

        String result = listCommand.handle(chatId, "/list");

        assertEquals("Список отслеживаемых ссылок пуст", result);
    }

    @Test
    void shouldReturnOnlyLinksWithRequestedTag() {
        ScrapperClient scrapperClient = mock(ScrapperClient.class);
        ListCommand listCommand = new ListCommand(scrapperClient);

        long chatId = 1L;

        ListLinksResponse response = new ListLinksResponse(
                List.of(new LinkResponse(
                        1L,
                        URI.create("https://github.com/openai/openai-java"),
                        List.of("work", "backend"),
                        List.of())),
                1);

        when(scrapperClient.getLinks(chatId, "work")).thenReturn(response);

        String result = listCommand.handle(chatId, "/list work");

        assertEquals("Отслеживаемые ссылки:\n" + "- https://github.com/openai/openai-java\n", result);
    }
}
