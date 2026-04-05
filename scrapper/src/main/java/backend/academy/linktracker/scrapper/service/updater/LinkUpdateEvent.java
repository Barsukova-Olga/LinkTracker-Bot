package backend.academy.linktracker.scrapper.service.updater;

import java.net.URI;
import java.time.Instant;
import java.util.List;

public record LinkUpdateEvent(
    long linkId,
    URI url,
    Instant occurredAt,
    String title,
    String author,
    String preview
) {}
