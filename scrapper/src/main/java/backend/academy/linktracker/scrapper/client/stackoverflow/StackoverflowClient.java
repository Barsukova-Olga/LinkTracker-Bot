package backend.academy.linktracker.scrapper.client.stackoverflow;

import backend.academy.linktracker.scrapper.client.stackoverflow.dto.StackoverflowQuestionResponse;
import backend.academy.linktracker.scrapper.client.stackoverflow.dto.StackoverflowQuestionsResponse;
import backend.academy.linktracker.scrapper.model.StackoverflowParsedLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class StackoverflowClient {

    private final RestClient stackoverflowRestClient;

    public StackoverflowQuestionResponse getQuestion(StackoverflowParsedLink link) {
        StackoverflowQuestionsResponse response = stackoverflowRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/2.3/questions/{id}")
                        .queryParam("site", "stackoverflow")
                        .build(link.questionId()))
                .retrieve()
                .body(StackoverflowQuestionsResponse.class);

        if (response == null || response.items() == null || response.items().isEmpty()) {
            return null;
        }

        return response.items().get(0);
    }
}
