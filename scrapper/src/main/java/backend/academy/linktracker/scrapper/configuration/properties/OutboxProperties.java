package backend.academy.linktracker.scrapper.configuration.properties;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.outbox")
public record OutboxProperties(boolean enabled, int batchSize, int maxAttempts, Duration pollInterval) {}
