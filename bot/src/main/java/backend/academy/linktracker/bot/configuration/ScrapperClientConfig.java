package backend.academy.linktracker.bot.configuration;

import backend.academy.linktracker.bot.configuration.properties.ScrapperProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ScrapperClientConfig {

    private final ScrapperProperties scrapperProperties;

    public ScrapperClientConfig(ScrapperProperties scrapperProperties) {
        this.scrapperProperties = scrapperProperties;
    }

    @Bean
    public RestClient scrapperRestClient() {
        return RestClient.builder().baseUrl(scrapperProperties.getBaseUrl()).build();
    }
}
