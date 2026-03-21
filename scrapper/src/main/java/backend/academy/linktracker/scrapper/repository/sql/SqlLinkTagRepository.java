package backend.academy.linktracker.scrapper.repository.sql;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.LinkTagRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "SQL")
public class SqlLinkTagRepository implements LinkTagRepository {

    private final JdbcTemplate jdbcTemplate;

    public SqlLinkTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addTag(long chatId, long linkId, String tag) {
        jdbcTemplate.update("""
            insert into link_tag(chat_id, link_id, tag)
            values (?, ?, ?)
            on conflict do nothing
            """, chatId, linkId, tag);
    }

    @Override
    public void removeTag(long chatId, long linkId, String tag) {
        jdbcTemplate.update("""
            delete from link_tag
            where chat_id = ? and link_id = ? and tag = ?
            """, chatId, linkId, tag);
    }

    @Override
    public List<String> findTags(long chatId, long linkId) {
        return jdbcTemplate.query("""
            select tag
            from link_tag
            where chat_id = ? and link_id = ?
            order by tag
            """, (rs, rowNum) -> rs.getString("tag"), chatId, linkId);
    }

    @Override
    public void removeAllTags(long chatId, long linkId) {
        jdbcTemplate.update("""
            delete from link_tag
            where chat_id = ? and link_id = ?
            """, chatId, linkId);
    }

    @Override
    public List<Link> findLinksByTag(long chatId, String tag) {
        String sql = """
        select l.id, l.url, l.last_updated_at, l.created_at
        from links l
        join chat_link cl on l.id = cl.link_id
        join link_tag lt on lt.chat_id = cl.chat_id and lt.link_id = cl.link_id
        where cl.chat_id = ? and lt.tag = ?
        order by l.id
        """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Link(
                        rs.getLong("id"),
                        rs.getString("url"),
                        rs.getObject("last_updated_at", OffsetDateTime.class) != null
                                ? rs.getObject("last_updated_at", OffsetDateTime.class)
                                        .toInstant()
                                : null,
                        rs.getObject("created_at", OffsetDateTime.class) != null
                                ? rs.getObject("created_at", OffsetDateTime.class)
                                        .toInstant()
                                : null),
                chatId,
                tag);
    }
}
