package backend.academy.linktracker.scrapper.client.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record GithubRepoResponse(@JsonProperty("updated_at") Instant updatedAt) {}
