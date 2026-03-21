package backend.academy.linktracker.scrapper.schedule;

import backend.academy.linktracker.scrapper.client.github.GithubClient;
import backend.academy.linktracker.scrapper.client.github.dto.GithubRepoResponse;
import backend.academy.linktracker.scrapper.client.stackoverflow.StackoverflowClient;
import backend.academy.linktracker.scrapper.client.stackoverflow.dto.StackoverflowQuestionResponse;
import backend.academy.linktracker.scrapper.link.parser.LinkParser;
import backend.academy.linktracker.scrapper.model.GithubParsedLink;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.model.ParsedLink;
import backend.academy.linktracker.scrapper.model.StackoverflowParsedLink;
import java.time.Instant;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultLinkUpdateChecker implements LinkUpdateChecker {

    private final GithubClient githubClient;
    private final StackoverflowClient stackoverflowClient;

    private final LinkParser linkParser;

    public DefaultLinkUpdateChecker(
            GithubClient githubClient, StackoverflowClient stackoverflowClient, LinkParser linkParser) {
        this.githubClient = githubClient;
        this.stackoverflowClient = stackoverflowClient;
        this.linkParser = linkParser;
    }

    @Override
    public Optional<Instant> getCurrentLastUpdatedAt(Link link) {
        Optional<ParsedLink> parsedLink = linkParser.parse(link.url());

        if (parsedLink.isEmpty()) {
            return Optional.empty();
        }

        ParsedLink value = parsedLink.orElseThrow();

        if (value instanceof GithubParsedLink githubLink) {
            try {
                log.info("Checking GitHub link: url={}", githubLink.uri());
                GithubRepoResponse response = githubClient.getRepository(githubLink);
                log.info("GitHub link checked: url={}, updatedAt={}", githubLink.uri(), response.updatedAt());

                return Optional.ofNullable(response.updatedAt());
            } catch (Exception e) {
                log.warn("Failed to check GitHub link: url={}, message={}", githubLink.uri(), e.getMessage());
                return Optional.empty();
            }
        }

        if (value instanceof StackoverflowParsedLink stackoverflowLink) {
            try {
                log.info("Checking StackOverflow link: url={}", stackoverflowLink.uri());
                StackoverflowQuestionResponse response = stackoverflowClient.getQuestion(stackoverflowLink);
                if (response == null) {
                    log.warn("StackOverflow link returned empty response: url={}", stackoverflowLink.uri());
                    return Optional.empty();
                }

                log.info(
                        "StackOverflow link checked: url={}, lastActivityDate={}",
                        stackoverflowLink.uri(),
                        response.lastActivityDate());
                return Optional.ofNullable(response.lastActivityDate());
            } catch (Exception e) {
                log.warn(
                        "Failed to check StackOverflow link: url={}, message={}",
                        stackoverflowLink.uri(),
                        e.getMessage());
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}
