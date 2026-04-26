package backend.academy.linktracker.scrapper.service.updater;

import backend.academy.linktracker.scrapper.configuration.properties.ScheduleProperties;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultLinkUpdaterService implements LinkUpdaterService {

    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;
    private final LinkUpdateProcessor linkUpdateProcessor;
    private final LinkUpdateTransactionService linkUpdateTransactionService;
    private final ScheduleProperties scheduleProperties;

    @Override
    public LinkUpdateReport update() {
        int batchSize = scheduleProperties.getBatchSize();
        int threads = scheduleProperties.getThreads();
        int offset = 0;

        AtomicInteger totalProcessed = new AtomicInteger();
        AtomicInteger totalUpdated = new AtomicInteger();
        List<String> failedLinks = Collections.synchronizedList(new ArrayList<>());

        while (true) {
            List<Link> links = linkRepository.findBatch(batchSize, offset);
            if (links.isEmpty()) {
                break;
            }

            List<List<Link>> chunks = split(links, threads);

            ExecutorService executor = Executors.newFixedThreadPool(threads);
            try {
                for (List<Link> chunk : chunks) {
                    executor.submit(() -> processChunk(chunk, totalProcessed, totalUpdated, failedLinks));
                }

                executor.shutdown();
                boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
                if (!finished) {
                    log.warn("Timeout while waiting for batch processing to finish");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }

            offset += batchSize;
        }

        return new LinkUpdateReport(totalProcessed.get(), totalUpdated.get(), failedLinks);
    }

    private void processChunk(
            List<Link> chunk, AtomicInteger totalProcessed, AtomicInteger totalUpdated, List<String> failedLinks) {
        for (Link link : chunk) {
            totalProcessed.incrementAndGet();
            try {
                Optional<LinkUpdateEvent> currentUpdated = linkUpdateProcessor.process(link);

                if (currentUpdated.isEmpty()) {
                    continue;
                }

                LinkUpdateEvent linkUpdateEvent = currentUpdated.orElseThrow();

                linkRepository.updateLastUpdatedAt(link.id(), linkUpdateEvent.occurredAt());

                List<Long> chatIds = chatLinkRepository.findChatsByLinkId(link.id());

                if (!chatIds.isEmpty()) {
                    String description = "%s%nАвтор: %s%nВремя: %s%n%n%s"
                            .formatted(
                                    nullToDefault(linkUpdateEvent.title(), "Новое обновление"),
                                    nullToDefault(linkUpdateEvent.author(), "Неизвестный автор"),
                                    linkUpdateEvent.occurredAt(),
                                    nullToDefault(linkUpdateEvent.preview(), "Превью отсутствует"));
                    boolean notificationCreated =
                            linkUpdateTransactionService.saveUpdateAndOutbox(link, linkUpdateEvent, description);

                    if (notificationCreated) {
                        totalUpdated.incrementAndGet();
                    }
                }
            } catch (Exception e) {
                failedLinks.add(link.url());
                log.warn("Failed to process link {}: {}", link.url(), e.getMessage());
            }
        }
    }

    private List<List<Link>> split(List<Link> links, int threads) {
        if (threads <= 0) {
            throw new IllegalArgumentException("threads must be > 0");
        }
        int size = links.size();
        int chunkSize = (size + threads - 1) / threads;

        List<List<Link>> result = new ArrayList<>();

        for (int i = 0; i < size; i += chunkSize) {
            result.add(links.subList(i, Math.min(i + chunkSize, size)));
        }

        return result;
    }

    private String nullToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
