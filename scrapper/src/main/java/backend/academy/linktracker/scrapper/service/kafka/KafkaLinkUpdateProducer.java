package backend.academy.linktracker.scrapper.service.kafka;

import backend.academy.linktracker.events.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.configuration.properties.KafkaTopicsProperties;
import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaLinkUpdateProducer {

    private final KafkaTemplate<Long, LinkUpdateEvent> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public void send(LinkUpdateRequest update) {
        LinkUpdateEvent event = LinkUpdateEvent.newBuilder()
                .setId(update.id())
                .setUrl(update.url().toString())
                .setDescription(update.description())
                .setTgChatIds(update.tgChatIds())
                .build();

        CompletableFuture<SendResult<Long, LinkUpdateEvent>> future =
                kafkaTemplate.send(topics.getLinkUpdates(), event.getId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                        "Sent link update id={} to topic={}, partition={}, offset={}",
                        event.getId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send link update id={} to Kafka", event.getId(), ex);
            }
        });
    }
}
