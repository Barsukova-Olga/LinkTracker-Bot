package backend.academy.linktracker.scrapper.service.updater;

import backend.academy.linktracker.scrapper.client.github.GithubClient;
import backend.academy.linktracker.scrapper.client.github.dto.GithubIssueResponse;
import backend.academy.linktracker.scrapper.client.stackoverflow.StackoverflowClient;
import backend.academy.linktracker.scrapper.client.stackoverflow.dto.StackoverflowAnswerResponse;
import backend.academy.linktracker.scrapper.client.stackoverflow.dto.StackoverflowAnswersResponse;
import backend.academy.linktracker.scrapper.client.stackoverflow.dto.StackoverflowQuestionResponse;
import backend.academy.linktracker.scrapper.link.parser.LinkParser;
import backend.academy.linktracker.scrapper.model.GithubParsedLink;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.model.ParsedLink;
import backend.academy.linktracker.scrapper.model.StackoverflowParsedLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DefaultLinkUpdateProcessor implements LinkUpdateProcessor {

    private final GithubClient githubClient;
    private final StackoverflowClient stackoverflowClient;
    private final LinkParser linkParser;

    public DefaultLinkUpdateProcessor(
        GithubClient githubClient,
        StackoverflowClient stackoverflowClient,
        LinkParser linkParser
    ) {
        this.githubClient = githubClient;
        this.stackoverflowClient = stackoverflowClient;
        this.linkParser = linkParser;
    }

    @Override
    public Optional<LinkUpdateEvent> process(Link link) {
        Optional<ParsedLink> parsedLink = linkParser.parse(link.url());

        if (parsedLink.isEmpty()) {
            return Optional.empty();
        }

        ParsedLink value = parsedLink.orElseThrow();

        if (value instanceof GithubParsedLink githubLink) {
            return processGithub(link, githubLink);
        }
        if (value instanceof StackoverflowParsedLink stackoverflowLink) {
            return processStackoverflow(link, stackoverflowLink);
        }

        return Optional.empty();
    }

    private Optional<LinkUpdateEvent> processGithub(Link link, GithubParsedLink githubLink) {
        try {

            log.info("Checking GitHub link: url={}", githubLink.uri());
            GithubIssueResponse[] response = githubClient.getIssues(githubLink);
            if(response.length == 0) {
                return Optional.empty();
            }
            GithubIssueResponse issue = response[0];
            if (link.lastUpdatedAt() != null && !issue.createdAt().isAfter(link.lastUpdatedAt())) {
                return Optional.empty();
            }

            log.info("GitHub link checked: url={}, updatedAt={}", githubLink.uri(), issue.createdAt());

            return Optional.of(new LinkUpdateEvent(link.id(), githubLink.uri(), issue.createdAt(),
                issue.title(), issue.user() == null ? null : issue.user().login() ,preview(issue.body())));
        } catch (Exception e) {
            log.warn("Failed to check GitHub link: url={}, message={}", githubLink.uri(), e.getMessage());
            return Optional.empty();
        }
    }
    private Optional<LinkUpdateEvent> processStackoverflow(Link link, StackoverflowParsedLink stackoverflowLink) {
        try {
            log.info("Checking StackOverflow link: url={}", stackoverflowLink.uri());
            StackoverflowQuestionResponse questionResponse = stackoverflowClient.getQuestion(stackoverflowLink);
            if (questionResponse == null) {
                log.warn("StackOverflow link returned empty response: url={}", stackoverflowLink.uri());
                return Optional.empty();
            }

            List<StackoverflowAnswerResponse> answersResponse = stackoverflowClient.getAnswers(stackoverflowLink);
            if (answersResponse == null || answersResponse.isEmpty()) {
                log.warn("StackOverflow link returned empty answers: url={}", stackoverflowLink.uri());
                return Optional.empty();
            }

            StackoverflowAnswerResponse answerResponse = answersResponse.getFirst();

            if (link.lastUpdatedAt() != null && !answerResponse.creationDate().isAfter(link.lastUpdatedAt())) {
                return Optional.empty();
            }

            log.info(
                "StackOverflow link checked: url={}, creationDate={}",
                stackoverflowLink.uri(),
                answerResponse.creationDate());
            return Optional.of(new LinkUpdateEvent(link.id(), stackoverflowLink.uri(), answerResponse.creationDate(),
                questionResponse.title(), answerResponse.owner() == null ? null : answerResponse.owner().displayName() ,preview(answerResponse.body())));
        } catch (Exception e) {
            log.warn(
                "Failed to check StackOverflow link: url={}, message={}",
                stackoverflowLink.uri(),
                e.getMessage());
            return Optional.empty();
        }
    }
    private String preview(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return text.length() <= 200 ? text : text.substring(0, 200);
    }

}
