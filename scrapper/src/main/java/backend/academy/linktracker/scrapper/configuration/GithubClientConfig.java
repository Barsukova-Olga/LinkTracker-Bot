package backend.academy.linktracker.scrapper.configuration;

import backend.academy.linktracker.scrapper.properties.GithubProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GithubClientConfig {

    private final GithubProperties githubProperties;

    public GithubClientConfig(GithubProperties githubProperties) {
        this.githubProperties = githubProperties;
    }

    @Bean
    public RestClient githubRestClient() {
        return RestClient.builder().baseUrl("https://api.github.com").build();
    }
}
