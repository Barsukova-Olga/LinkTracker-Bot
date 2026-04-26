package backend.academy.linktracker.scrapper.outbox;

import java.time.OffsetDateTime;

public record OutboxMessage(
        long id,
        String topic,
        String messageKey,
        String payload,
        OutboxStatus status,
        int attempts,
        String lastError,
        OffsetDateTime createdAt,
        OffsetDateTime sentAt) {}
