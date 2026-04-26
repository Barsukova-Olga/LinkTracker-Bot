package backend.academy.linktracker.scrapper.kafka;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.events.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.configuration.properties.KafkaTopicsProperties;
import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.service.kafka.KafkaLinkUpdateProducer;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class KafkaLinkUpdateProducerTest {
    ;

    @Test
    void shouldSendMessageToKafka() {
        @SuppressWarnings("unchecked")
        KafkaTemplate<Long, LinkUpdateEvent> kafkaTemplate = mock(KafkaTemplate.class);
        KafkaTopicsProperties topics = new KafkaTopicsProperties();
        topics.setLinkUpdates("link-updates-test");

        when(kafkaTemplate.send(eq("link-updates-test"), eq(1L), any(LinkUpdateEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        KafkaLinkUpdateProducer producer = new KafkaLinkUpdateProducer(kafkaTemplate, topics);
        LinkUpdateRequest update =
                new LinkUpdateRequest(1L, URI.create("https://example.com"), "test description", List.of(123L));

        producer.send(update);
        ArgumentCaptor<LinkUpdateEvent> eventCaptor = ArgumentCaptor.forClass(LinkUpdateEvent.class);
        verify(kafkaTemplate).send(eq("link-updates-test"), eq(1L), eventCaptor.capture());

        LinkUpdateEvent captured = eventCaptor.getValue();
        assertThat(captured.getId()).isEqualTo(1L);
        assertThat(captured.getUrl().toString()).isEqualTo("https://example.com");
        assertThat(captured.getDescription().toString()).isEqualTo("test description");
        assertThat(captured.getTgChatIds().getFirst()).isEqualTo(123L);
    }
}
