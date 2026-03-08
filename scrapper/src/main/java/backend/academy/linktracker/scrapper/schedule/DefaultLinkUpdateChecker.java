package backend.academy.linktracker.scrapper.schedule;

import backend.academy.linktracker.scrapper.client.github.GithubClient;
import backend.academy.linktracker.scrapper.client.github.dto.GithubRepoResponse;
import backend.academy.linktracker.scrapper.client.stackoverflow.StackoverflowClient;
import backend.academy.linktracker.scrapper.client.stackoverflow.dto.StackoverflowQuestionResponse;
import backend.academy.linktracker.scrapper.model.GithubParsedLink;
import backend.academy.linktracker.scrapper.model.ParsedLink;
import backend.academy.linktracker.scrapper.model.StackoverflowParsedLink;
import backend.academy.linktracker.scrapper.model.TrackedParsedLink;
import java.time.Instant;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultLinkUpdateChecker implements LinkUpdateChecker {

    private final GithubClient githubClient;
    private final StackoverflowClient stackoverflowClient;

    public DefaultLinkUpdateChecker(GithubClient githubClient, StackoverflowClient stackoverflowClient) {
        this.githubClient = githubClient;
        this.stackoverflowClient = stackoverflowClient;
    }

    @Override
    public Optional<Instant> getCurrentLastUpdatedAt(TrackedParsedLink link) {
        ParsedLink parsedLink = link.parsedLink();

        if (parsedLink instanceof GithubParsedLink githubParsedLink) {
            try {
                log.info("Checking GitHub link: url={}", githubParsedLink.uri());
                GithubRepoResponse response = githubClient.getRepository(githubParsedLink);
                log.info("GitHub link checked: url={}, updatedAt={}", githubParsedLink.uri(), response.updatedAt());

                return Optional.ofNullable(response.updatedAt());
            } catch (Exception e) {
                log.warn("Failed to check GitHub link: url={}, message={}", githubParsedLink.uri(), e.getMessage());
                return Optional.empty();
            }
        }

        if (parsedLink instanceof StackoverflowParsedLink stackoverflowParsedLink) {
            try {
                log.info("Checking StackOverflow link: url={}", stackoverflowParsedLink.uri());
                StackoverflowQuestionResponse response = stackoverflowClient.getQuestion(stackoverflowParsedLink);
                if (response == null) {
                    log.warn("StackOverflow link returned empty response: url={}", stackoverflowParsedLink.uri());
                    return Optional.empty();
                }

                log.info(
                        "StackOverflow link checked: url={}, lastActivityDate={}",
                        stackoverflowParsedLink.uri(),
                        response.lastActivityDate());
                return Optional.ofNullable(response.lastActivityDate());
            } catch (Exception e) {
                log.warn(
                        "Failed to check StackOverflow link: url={}, message={}",
                        stackoverflowParsedLink.uri(),
                        e.getMessage());
                return Optional.empty();
            }
        }
        log.warn("Unsupported parsed link type: url={}", parsedLink.uri());
        return Optional.empty();
    }
}
