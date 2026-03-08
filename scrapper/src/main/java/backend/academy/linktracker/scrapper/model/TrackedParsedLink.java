package backend.academy.linktracker.scrapper.model;

import java.time.Instant;
import java.util.List;

public record TrackedParsedLink(
        long id, ParsedLink parsedLink, List<String> tags, List<String> filters, Instant lastUpdatedAt) {}
