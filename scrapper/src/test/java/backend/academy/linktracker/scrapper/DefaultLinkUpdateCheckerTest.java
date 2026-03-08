package backend.academy.linktracker.scrapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import backend.academy.linktracker.scrapper.client.github.GithubClient;
import backend.academy.linktracker.scrapper.client.stackoverflow.StackoverflowClient;
import backend.academy.linktracker.scrapper.model.GithubParsedLink;
import backend.academy.linktracker.scrapper.model.TrackedParsedLink;
import backend.academy.linktracker.scrapper.schedule.DefaultLinkUpdateChecker;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class DefaultLinkUpdateCheckerTest {

    @Test
    void shouldReturnEmptyWhenGithubClientThrowsException() {
        GithubClient githubClient = mock(GithubClient.class);
        StackoverflowClient stackoverflowClient = mock(StackoverflowClient.class);

        DefaultLinkUpdateChecker checker = new DefaultLinkUpdateChecker(githubClient, stackoverflowClient);

        TrackedParsedLink link = new TrackedParsedLink(
                1L,
                new GithubParsedLink(URI.create("https://github.com/openai/openai-java"), "openai", "openai-java"),
                List.of("work"),
                List.of(),
                Instant.parse("2024-01-01T00:00:00Z"));

        when(githubClient.getRepository((GithubParsedLink) link.parsedLink()))
                .thenThrow(new RuntimeException("GitHub API failed"));

        Optional<Instant> result = checker.getCurrentLastUpdatedAt(link);

        assertTrue(result.isEmpty());
    }
}
