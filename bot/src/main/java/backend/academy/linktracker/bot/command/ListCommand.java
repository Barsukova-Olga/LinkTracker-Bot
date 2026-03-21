package backend.academy.linktracker.bot.command;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.LinkResponse;
import backend.academy.linktracker.bot.dto.ListLinksResponse;
import org.springframework.stereotype.Component;

@Component
public class ListCommand implements BotCommand {

    private final ScrapperClient scrapperClient;

    public ListCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String handle(long chatId, String text) {
        String tag = extractTag(text);
        System.out.println(tag);
        ListLinksResponse response = scrapperClient.getLinks(chatId, tag);

        if (response == null || response.links() == null || response.links().isEmpty()) {
            return "Список отслеживаемых ссылок пуст";
        }

        StringBuilder sb = new StringBuilder("Отслеживаемые ссылки:\n");
        for (LinkResponse link : response.links()) {
            sb.append("- ").append(link.url()).append("\n");
        }
        return sb.toString();
    }

    private String extractTag(String text) {
        if (text == null) {
            return null;
        }

        String trimmed = text.trim();
        if (trimmed.equals("/list")) {
            return null;
        }

        String[] parts = trimmed.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            return null;
        }

        return parts[1].trim();
    }
}
