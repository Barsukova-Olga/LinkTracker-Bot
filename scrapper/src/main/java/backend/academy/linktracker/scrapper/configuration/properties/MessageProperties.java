package backend.academy.linktracker.scrapper.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record MessageProperties(
    MessageTransport messageTransport
) {}
