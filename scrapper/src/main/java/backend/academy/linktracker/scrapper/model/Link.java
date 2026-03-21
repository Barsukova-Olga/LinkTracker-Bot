package backend.academy.linktracker.scrapper.model;

import java.time.Instant;

public record Link(long id, String url, Instant lastUpdatedAt, Instant createdAt) {}
