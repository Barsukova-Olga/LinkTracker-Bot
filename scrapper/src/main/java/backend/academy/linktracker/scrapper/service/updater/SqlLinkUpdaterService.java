package backend.academy.linktracker.scrapper.service.updater;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.*;
import backend.academy.linktracker.scrapper.schedule.LinkUpdateChecker;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "SQL")
public class SqlLinkUpdaterService implements LinkUpdaterService {

    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;
    private final LinkUpdateChecker linkUpdateChecker;
    private final BotClient botClient;

    public SqlLinkUpdaterService(
            LinkRepository linkRepository,
            ChatLinkRepository chatLinkRepository,
            LinkUpdateChecker linkUpdateChecker,
            BotClient botClient) {
        this.linkRepository = linkRepository;
        this.chatLinkRepository = chatLinkRepository;
        this.linkUpdateChecker = linkUpdateChecker;
        this.botClient = botClient;
    }

    @Override
    @Transactional
    public void update() {
        List<Link> links = linkRepository.findAll();

        for (Link link : links) {
            Optional<Instant> currentUpdatedAt = linkUpdateChecker.getCurrentLastUpdatedAt(link);

            if (currentUpdatedAt.isEmpty()) {
                continue;
            }

            Instant newUpdatedAt = currentUpdatedAt.orElseThrow();
            Instant oldUpdatedAt = link.lastUpdatedAt();

            if (oldUpdatedAt == null || newUpdatedAt.isAfter(oldUpdatedAt)) {
                linkRepository.updateLastUpdatedAt(link.id(), newUpdatedAt);

                List<Long> chatIds = chatLinkRepository.findChatsByLinkId(link.id());

                if (!chatIds.isEmpty()) {
                    botClient.sendUpdate(
                            new LinkUpdateRequest(link.id(), URI.create(link.url()), "Ссылка обновилась", chatIds));
                }
            }
        }
    }
}
