package backend.academy.linktracker.scrapper.client.stackoverflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StackoverflowOwnerResponse(
    @JsonProperty("display_name") String displayName
) {}
