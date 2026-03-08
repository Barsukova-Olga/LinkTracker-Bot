package backend.academy.linktracker.scrapper.client.github;

import backend.academy.linktracker.scrapper.client.github.dto.GithubRepoResponse;
import backend.academy.linktracker.scrapper.model.GithubParsedLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class GithubClient {

    private final RestClient githubRestClient;

    public GithubRepoResponse getRepository(GithubParsedLink link) {
        return githubRestClient
                .get()
                .uri("/repos/{owner}/{repo}", link.owner(), link.repo())
                .retrieve()
                .body(GithubRepoResponse.class);
    }
}
