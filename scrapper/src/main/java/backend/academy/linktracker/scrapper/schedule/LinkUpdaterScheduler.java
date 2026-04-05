package backend.academy.linktracker.scrapper.schedule;

import backend.academy.linktracker.scrapper.service.updater.LinkUpdateReport;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdaterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.schedule", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LinkUpdaterScheduler {

    private final LinkUpdaterService linkUpdaterService;

    public LinkUpdaterScheduler(LinkUpdaterService linkUpdateService) {
        this.linkUpdaterService = linkUpdateService;
    }

    @Scheduled(fixedDelayString = "${app.schedule.interval}")
    public void update() {
        log.info("Scheduler started");
        LinkUpdateReport report = linkUpdaterService.update();
        log.info(
                "Update finished: processed={}, updated={}, failed={}",
                report.totalProcessed(),
                report.totalUpdated(),
                report.failedLinks().size());
        if (!report.failedLinks().isEmpty()) {
            log.warn("Failed links: {}", report.failedLinks());
        }
    }
}
