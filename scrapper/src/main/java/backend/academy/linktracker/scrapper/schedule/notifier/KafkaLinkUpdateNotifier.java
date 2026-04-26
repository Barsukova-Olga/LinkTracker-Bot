package backend.academy.linktracker.scrapper.schedule.notifier;

import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.service.kafka.KafkaLinkUpdateProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(
    prefix = "app",
    name = "message-transport",
    havingValue = "kafka",
    matchIfMissing = true
)
@RequiredArgsConstructor
public class KafkaLinkUpdateNotifier implements LinkUpdateNotifier {

    private final KafkaLinkUpdateProducer producer;

    @Override
    public void notifyUpdate(LinkUpdateRequest update) {
        producer.send(update);
    }
}
