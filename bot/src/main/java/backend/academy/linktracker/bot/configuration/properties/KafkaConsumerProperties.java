package backend.academy.linktracker.bot.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.consumer")
public record KafkaConsumerProperties(long retryAttempts, long retryBackoffMs) {}
