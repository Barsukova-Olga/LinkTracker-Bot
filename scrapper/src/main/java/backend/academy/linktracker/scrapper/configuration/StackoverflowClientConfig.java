package backend.academy.linktracker.scrapper.configuration;

import backend.academy.linktracker.scrapper.properties.StackoverflowProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class StackoverflowClientConfig {

    private final StackoverflowProperties stackoverflowProperties;

    public StackoverflowClientConfig(StackoverflowProperties stackoverflowProperties) {
        this.stackoverflowProperties = stackoverflowProperties;
    }

    @Bean
    public RestClient stackoverflowRestClient() {
        return RestClient.builder().baseUrl("https://api.stackexchange.com").build();
    }
}
