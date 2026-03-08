package backend.academy.linktracker.scrapper.client.stackoverflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record StackoverflowQuestionResponse(
        @JsonProperty("last_activity_date") long lastActivityDateEpoch) {
    public Instant lastActivityDate() {
        return Instant.ofEpochSecond(lastActivityDateEpoch);
    }
}
