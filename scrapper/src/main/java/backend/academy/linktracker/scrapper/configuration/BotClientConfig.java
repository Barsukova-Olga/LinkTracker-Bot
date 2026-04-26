package backend.academy.linktracker.scrapper.configuration;

import backend.academy.linktracker.scrapper.configuration.properties.BotProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BotClientConfig {

    private final BotProperties botProperties;

    public BotClientConfig(BotProperties botProperties) {
        this.botProperties = botProperties;
    }

    @Bean
    public RestClient botRestClient() {
        return RestClient.builder().baseUrl(botProperties.getBaseUrl()).build();
    }
}
