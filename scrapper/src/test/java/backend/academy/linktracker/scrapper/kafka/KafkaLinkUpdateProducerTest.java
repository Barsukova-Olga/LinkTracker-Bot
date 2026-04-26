package backend.academy.linktracker.scrapper.kafka;

import backend.academy.linktracker.scrapper.configuration.KafkaTopicConfig;
import backend.academy.linktracker.scrapper.configuration.properties.KafkaTopicsProperties;
import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.service.kafka.KafkaLinkUpdateProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import org.testcontainers.utility.DockerImageName;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
class KafkaLinkUpdateProducerTest extends AbstractKafkaPostgresSpringBootTest{

    @Autowired
    private KafkaLinkUpdateProducer producer;
    ;

    @Test
    void shouldSendMessageToKafka() {
        LinkUpdateRequest update = new LinkUpdateRequest(
            1L,
            URI.create("https://example.com"),
            "test description",
            List.of(123L)
        );

        producer.send(update);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        try (KafkaConsumer<Long, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of("link-updates-test"));

            ConsumerRecords<Long, String> records =
                consumer.poll(Duration.ofSeconds(10));

            assertThat(records.count()).isEqualTo(1);

            ConsumerRecord<Long, String> record = records.iterator().next();

            assertThat(record.key()).isEqualTo(1L);
            assertThat(record.value()).contains("https://example.com");
        }
    }
}
