package backend.academy.linktracker.bot.controller;

import backend.academy.linktracker.bot.dto.LinkUpdateRequest;
import backend.academy.linktracker.events.LinkUpdateEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import java.net.URI;

@Validated
@Component
@RequiredArgsConstructor
public class UpdateKafkaListener {
    private final UpdatesService updatesService;

    @KafkaListener(topics = "${app.kafka.topics.link-updates}")
    public void listen(LinkUpdateEvent event) {
        updatesService.handleUpdate(new LinkUpdateRequest(
            event.getId(),
            URI.create(event.getUrl().toString()),
            event.getDescription().toString(),
            event.getTgChatIds()
        ));
    }

}
