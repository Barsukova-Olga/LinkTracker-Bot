package backend.academy.linktracker.scrapper.outbox;

import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcOutboxRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public void save(String topic, String messageKey, LinkUpdateRequest payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);

            jdbcTemplate.update(
                """
                INSERT INTO outbox_messages(topic, message_key, payload, status)
                VALUES (?, ?, ?::jsonb, ?)
                """,
                topic,
                messageKey,
                json,
                OutboxStatus.NEW.name()
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
    }

    public List<OutboxMessage> findNewMessages(int limit) {
        return jdbcTemplate.query(
            """
            SELECT id, topic, message_key, payload::text, status, attempts, last_error, created_at, sent_at
            FROM outbox_messages
            WHERE status = 'NEW'
            ORDER BY created_at
            LIMIT ?
            FOR UPDATE SKIP LOCKED
            """,
            this::mapRow,
            limit
        );
    }

    public void markSent(long id) {
        jdbcTemplate.update(
            """
            UPDATE outbox_messages
            SET status = 'SENT',
                sent_at = now(),
                last_error = null
            WHERE id = ?
            """,
            id
        );
    }

    public void markFailed(long id, String error) {
        jdbcTemplate.update(
            """
            UPDATE outbox_messages
            SET status = 'FAILED',
                attempts = attempts + 1,
                last_error = ?
            WHERE id = ?
            """,
            error,
            id
        );
    }

    public void incrementAttempts(long id, String error) {
        jdbcTemplate.update(
            """
            UPDATE outbox_messages
            SET attempts = attempts + 1,
                last_error = ?
            WHERE id = ?
            """,
            error,
            id
        );
    }

    private OutboxMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OutboxMessage(
            rs.getLong("id"),
            rs.getString("topic"),
            rs.getString("message_key"),
            rs.getString("payload"),
            OutboxStatus.valueOf(rs.getString("status")),
            rs.getInt("attempts"),
            rs.getString("last_error"),
            rs.getObject("created_at", OffsetDateTime.class),
            rs.getObject("sent_at", OffsetDateTime.class)
        );
    }
}
