package backend.academy.linktracker.scrapper.schedule;

import backend.academy.linktracker.scrapper.model.TrackedParsedLink;
import backend.academy.linktracker.scrapper.repository.ScrapperRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LinkUpdaterScheduler {

    private final ScrapperRepository scrapperRepository;
    private final LinkUpdateNotifier notifyUpdate;
    private final LinkUpdateChecker linkUpdateChecker;

    public LinkUpdaterScheduler(
            ScrapperRepository scrapperRepository,
            LinkUpdateNotifier notifyUpdate,
            LinkUpdateChecker linkUpdateChecker) {
        this.scrapperRepository = scrapperRepository;
        this.notifyUpdate = notifyUpdate;
        this.linkUpdateChecker = linkUpdateChecker;
    }

    @Scheduled(fixedDelayString = "${app.schedule.interval}")
    public void update() {
        Map<Long, List<TrackedParsedLink>> allLinks = scrapperRepository.findAll();

        log.info("Scheduler started, chats={}", allLinks.size());

        for (Map.Entry<Long, List<TrackedParsedLink>> entry : allLinks.entrySet()) {
            long chatId = entry.getKey();
            List<TrackedParsedLink> links = entry.getValue();

            for (TrackedParsedLink link : links) {
                Optional<Instant> currentLastUpdatedAt = linkUpdateChecker.getCurrentLastUpdatedAt(link);

                if (currentLastUpdatedAt.isEmpty()) {
                    continue;
                }

                currentLastUpdatedAt.ifPresent(newValue -> {
                    Instant oldValue = link.lastUpdatedAt();

                    if (oldValue == null) {
                        scrapperRepository.updateLastUpdatedAt(
                                chatId, link.parsedLink().uri(), newValue);
                        return;
                    }

                    if (newValue.isAfter(oldValue)) {
                        scrapperRepository.updateLastUpdatedAt(
                                chatId, link.parsedLink().uri(), newValue);
                        notifyUpdate.notifyUpdate(chatId, link);
                    }
                });
            }
        }
    }
}
