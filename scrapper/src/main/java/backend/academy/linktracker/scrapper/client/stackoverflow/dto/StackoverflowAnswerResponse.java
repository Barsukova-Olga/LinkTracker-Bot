package backend.academy.linktracker.scrapper.client.stackoverflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record StackoverflowAnswerResponse(
    @JsonProperty("answer_id") long answerId,
    String body,
    StackoverflowOwnerResponse owner,
    @JsonProperty("creation_date") long creationDateEpoch
) {
    public Instant creationDate() {
        return Instant.ofEpochSecond(creationDateEpoch);
    }
}
