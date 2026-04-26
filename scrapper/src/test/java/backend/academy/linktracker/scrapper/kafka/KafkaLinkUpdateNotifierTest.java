package backend.academy.linktracker.scrapper.kafka;

import static org.mockito.Mockito.verify;

import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.schedule.notifier.KafkaLinkUpdateNotifier;
import backend.academy.linktracker.scrapper.service.kafka.KafkaLinkUpdateProducer;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KafkaLinkUpdateNotifierTest {

    @Mock
    private KafkaLinkUpdateProducer producer;

    @InjectMocks
    private KafkaLinkUpdateNotifier notifier;

    @Test
    void shouldSendUpdateToKafkaProducer() {
        LinkUpdateRequest update = new LinkUpdateRequest(1L, URI.create("https://example.com"), "test", List.of(123L));

        notifier.notifyUpdate(update);

        verify(producer).send(update);
    }
}
