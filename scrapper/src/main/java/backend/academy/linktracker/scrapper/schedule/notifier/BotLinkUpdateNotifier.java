package backend.academy.linktracker.scrapper.schedule.notifier;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.model.TrackedParsedLink;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BotLinkUpdateNotifier implements LinkUpdateNotifier {

    private final BotClient botClient;

    public BotLinkUpdateNotifier(BotClient botClient) {
        this.botClient = botClient;
    }

    @Override
    public void notifyUpdate(long chatId, TrackedParsedLink link) {
        botClient.sendUpdate(
                new LinkUpdateRequest(link.id(), link.parsedLink().uri(), "Обнаружено обновление", List.of(chatId)));
    }
}
