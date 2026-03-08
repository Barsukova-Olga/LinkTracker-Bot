package backend.academy.linktracker.bot.command;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.LinkResponse;
import backend.academy.linktracker.bot.dto.ListLinksResponse;
import java.util.List;
import java.util.stream.Collectors;
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
        ListLinksResponse response = scrapperClient.getLinks(chatId);

        if (response == null || response.links() == null || response.links().isEmpty()) {
            return "Список отслеживаемых ссылок пуст";
        }
        String tag = extractTag(text);

        List<LinkResponse> links = response.links();
        if (tag != null) {
            links = links.stream()
                    .filter(link -> link.tags() != null
                            && link.tags().stream().anyMatch(t -> t != null && t.equalsIgnoreCase(tag)))
                    .collect(Collectors.toList());
        }

        if (links.isEmpty()) {
            return "Список отслеживаемых ссылок пуст";
        }

        StringBuilder sb = new StringBuilder("Отслеживаемые ссылки:\n");
        for (LinkResponse link : links) {
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
