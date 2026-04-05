package backend.academy.linktracker.scrapper.update;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.client.github.GithubClient;
import backend.academy.linktracker.scrapper.client.stackoverflow.StackoverflowClient;
import backend.academy.linktracker.scrapper.link.parser.LinkParser;
import backend.academy.linktracker.scrapper.model.GithubParsedLink;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.schedule.DefaultLinkUpdateChecker;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class DefaultLinkUpdateCheckerTest {

    @Test
    void shouldReturnEmptyWhenGithubClientThrowsException() {
        GithubClient githubClient = mock(GithubClient.class);
        StackoverflowClient stackoverflowClient = mock(StackoverflowClient.class);
        LinkParser linkParser = mock(LinkParser.class);

        DefaultLinkUpdateChecker checker = new DefaultLinkUpdateChecker(githubClient, stackoverflowClient, linkParser);

        Link link = new Link(
                1L,
                "https://github.com/openai/openai-java",
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z"));

        GithubParsedLink parsedLink =
                new GithubParsedLink(URI.create("https://github.com/openai/openai-java"), "openai", "openai-java");

        when(linkParser.parse(link.url())).thenReturn(Optional.of(parsedLink));
        when(githubClient.getRepository(parsedLink)).thenThrow(new RuntimeException("GitHub API failed"));

        Optional<Instant> result = checker.getCurrentLastUpdatedAt(link);

        assertTrue(result.isEmpty());
    }
}
