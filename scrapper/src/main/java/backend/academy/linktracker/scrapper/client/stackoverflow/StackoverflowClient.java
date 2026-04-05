package backend.academy.linktracker.scrapper.client.stackoverflow;

import backend.academy.linktracker.scrapper.client.stackoverflow.dto.StackoverflowAnswerResponse;
import backend.academy.linktracker.scrapper.client.stackoverflow.dto.StackoverflowAnswersResponse;
import backend.academy.linktracker.scrapper.client.stackoverflow.dto.StackoverflowQuestionResponse;
import backend.academy.linktracker.scrapper.client.stackoverflow.dto.StackoverflowQuestionsResponse;
import backend.academy.linktracker.scrapper.model.StackoverflowParsedLink;
import java.util.List;
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
                .uri("/questions/{id}?site=stackoverflow", link.questionId())
                .retrieve()
                .body(StackoverflowQuestionsResponse.class);

        if (response == null || response.items() == null || response.items().isEmpty()) {
            return null;
        }

        return response.items().get(0);
    }

    public List<StackoverflowAnswerResponse> getAnswers(StackoverflowParsedLink link) {
        StackoverflowAnswersResponse response = stackoverflowRestClient
                .get()
                .uri("/questions/{id}/answers?site=stackoverflow&sort=creation&order=desc", link.questionId())
                .retrieve()
                .body(StackoverflowAnswersResponse.class);

        if (response == null || response.items() == null) {
            return List.of();
        }

        return response.items();
    }
}
