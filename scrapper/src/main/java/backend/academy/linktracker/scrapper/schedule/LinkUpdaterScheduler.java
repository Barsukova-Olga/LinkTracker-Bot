package backend.academy.linktracker.scrapper.schedule;

import backend.academy.linktracker.scrapper.service.updater.LinkUpdaterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.schedule", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LinkUpdaterScheduler {

    private final LinkUpdaterService linkUpdateService;

    public LinkUpdaterScheduler(LinkUpdaterService linkUpdateService) {
        this.linkUpdateService = linkUpdateService;
    }

    @Scheduled(fixedDelayString = "${app.schedule.interval}")
    public void update() {
        log.info("Scheduler started");
        linkUpdateService.update();
    }
}
