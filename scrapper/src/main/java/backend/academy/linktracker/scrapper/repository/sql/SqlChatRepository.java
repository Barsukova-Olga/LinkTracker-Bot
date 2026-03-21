package backend.academy.linktracker.scrapper.repository.sql;

import backend.academy.linktracker.scrapper.repository.ChatRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "SQL")
public class SqlChatRepository implements ChatRepository {

    private final JdbcTemplate jdbcTemplate;

    public SqlChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(long chatId) {
        jdbcTemplate.update("insert into chats(id) values (?)", chatId);
    }

    @Override
    public void remove(long chatId) {
        jdbcTemplate.update("delete from chats where id = ?", chatId);
    }

    @Override
    public boolean exists(long chatId) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from chats where id = ?", Integer.class, chatId);

        return count != null && count > 0;
    }
}
