package backend.academy.linktracker.scrapper.configuration;

import backend.academy.linktracker.scrapper.configuration.properties.BotProperties;
import backend.academy.linktracker.scrapper.configuration.properties.KafkaTopicsProperties;
import org.springframework.beans.factory.annotation.Value;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic linkUpdatesTopic(KafkaTopicsProperties properties) {
        return TopicBuilder.name(properties.getLinkUpdates())
            .partitions(3)
            .replicas(3)
            .config("min.insync.replicas", "2")
            .build();
    }

    @Bean
    public NewTopic linkUpdatesDltTopic(KafkaTopicsProperties properties) {
        return TopicBuilder.name(properties.getLinkUpdates() + ".DLT")
            .partitions(3)
            .replicas(3)
            .config("min.insync.replicas", "2")
            .build();
    }
}
