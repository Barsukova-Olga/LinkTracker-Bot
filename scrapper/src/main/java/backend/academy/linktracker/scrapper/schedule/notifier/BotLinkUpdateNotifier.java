package backend.academy.linktracker.scrapper.schedule.notifier;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "message-transport", havingValue = "http")
@RequiredArgsConstructor
public class BotLinkUpdateNotifier implements LinkUpdateNotifier {

    private final BotClient botClient;

    @Override
    public void notifyUpdate(LinkUpdateRequest linkUpdateRequest) {
        botClient.sendUpdate(linkUpdateRequest);
    }
}
