package backend.academy.linktracker.scrapper.outbox;

import backend.academy.linktracker.scrapper.configuration.properties.OutboxProperties;
import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(OutboxProperties.class)
@ConditionalOnProperty(prefix = "app.outbox", name = "enabled", havingValue = "true")
public class OutboxPublisher {

    private final JdbcOutboxRepository outboxRepository;
    private final KafkaTemplate<Long, LinkUpdateRequest> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OutboxProperties properties;

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval}")
    @Transactional
    public void publish() {
        List<OutboxMessage> messages =
            outboxRepository.findNewMessages(properties.batchSize());

        for (OutboxMessage message : messages) {
            publishOne(message);
        }
    }

    private void publishOne(OutboxMessage message) {
        try {
            LinkUpdateRequest request =
                objectMapper.readValue(message.payload(), LinkUpdateRequest.class);

            kafkaTemplate.send(
                message.topic(),
                Long.valueOf(message.messageKey()),
                request
            ).whenComplete((result, ex) -> {
                if (ex == null) {
                    outboxRepository.markSent(message.id());
                    log.info(
                        "Outbox message sent. id={}, topic={}, offset={}",
                        message.id(),
                        message.topic(),
                        result.getRecordMetadata().offset()
                    );
                } else {
                    handleFailure(message, ex);
                }
            });

        } catch (Exception e) {
            handleFailure(message, e);
        }
    }

    private void handleFailure(OutboxMessage message, Throwable ex) {
        String error = ex.getMessage();

        if (message.attempts() + 1 >= properties.maxAttempts()) {
            outboxRepository.markFailed(message.id(), error);
            log.error("Outbox message failed permanently. id={}", message.id(), ex);
        } else {
            outboxRepository.incrementAttempts(message.id(), error);
            log.warn("Outbox message send failed. id={}, attempt={}", message.id(), message.attempts() + 1, ex);
        }
    }
}
