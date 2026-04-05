package backend.academy.linktracker.scrapper.client.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record GithubIssueResponse(
        long id,
        String title,
        String body,
        GithubUserResponse user,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("pull_request") Object pullRequest) {}
