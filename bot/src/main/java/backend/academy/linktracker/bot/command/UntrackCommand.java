package backend.academy.linktracker.bot.command;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.RemoveLinkRequest;
import backend.academy.linktracker.bot.link.TrackedLink;
import backend.academy.linktracker.bot.link.parser.LinkParser;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class UntrackCommand implements BotCommand {

    private final LinkParser linkParser;
    private final ScrapperClient scrapperClient;

    public UntrackCommand(LinkParser linkParser, ScrapperClient scrapperClient) {
        this.linkParser = linkParser;
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String handle(long chatId, String text) {
        String[] parts = text.trim().split("\\s+", 2);
        if (parts.length < 2) {
            return "Укажите ссылку для удаления из отслеживания";
        }

        Optional<TrackedLink> trackedLink = linkParser.parse(parts[1]);

        if (trackedLink.isEmpty()) {
            return "Ссылка некорректна. Поддерживаются GitHub и StackOverflow.";
        }

        TrackedLink parsed = trackedLink.orElseThrow();
        try {
            scrapperClient.removeLink(chatId, new RemoveLinkRequest(parsed.getUrl()));
            return "Ссылка удалена из отслеживания";
        } catch (HttpClientErrorException.NotFound e) {
            return "Ссылка не отслеживается";
        }
    }
}
