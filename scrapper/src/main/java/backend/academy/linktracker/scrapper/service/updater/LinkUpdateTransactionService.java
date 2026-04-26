package backend.academy.linktracker.scrapper.service.updater;

import backend.academy.linktracker.scrapper.configuration.properties.KafkaTopicsProperties;
import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.outbox.OutboxService;
import backend.academy.linktracker.scrapper.repository.ChatLinkRepository;
import backend.academy.linktracker.scrapper.repository.LinkRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdateTransactionService {

    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;
    private final OutboxService outboxService;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Transactional
    public boolean saveUpdateAndOutbox(Link link, LinkUpdateEvent event, String description) {
        linkRepository.updateLastUpdatedAt(link.id(), event.occurredAt());

        List<Long> chatIds = chatLinkRepository.findChatsByLinkId(link.id());

        if (chatIds.isEmpty()) {
            return false;
        }

        outboxService.saveLinkUpdate(
                kafkaTopicsProperties.getLinkUpdates(),
                new LinkUpdateRequest(event.linkId(), event.url(), description, chatIds));

        return true;
    }
}
