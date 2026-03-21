package backend.academy.linktracker.scrapper.repository.sql;

import backend.academy.linktracker.scrapper.repository.ChatLinkRepository;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "SQL")
public class SqlChatLinkRepository implements ChatLinkRepository {

    private final JdbcTemplate jdbcTemplate;

    public SqlChatLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(long chatId, long linkId) {
        jdbcTemplate.update("insert into chat_link(chat_id, link_id) values (?, ?)", chatId, linkId);
    }

    @Override
    public void remove(long chatId, long linkId) {
        jdbcTemplate.update("delete from chat_link where chat_id = ? and link_id = ?", chatId, linkId);
    }

    @Override
    public List<Long> findLinksByChatId(long chatId) {
        return jdbcTemplate.query(
                "select link_id from chat_link where chat_id = ?", (rs, rowNum) -> rs.getLong("link_id"), chatId);
    }

    @Override
    public List<Long> findChatsByLinkId(long linkId) {
        return jdbcTemplate.query(
                "select chat_id from chat_link where link_id = ?", (rs, rowNum) -> rs.getLong("chat_id"), linkId);
    }
}
