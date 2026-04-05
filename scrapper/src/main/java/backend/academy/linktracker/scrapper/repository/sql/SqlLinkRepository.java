package backend.academy.linktracker.scrapper.repository.sql;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.LinkRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "SQL")
public class SqlLinkRepository implements LinkRepository {

    private final JdbcTemplate jdbcTemplate;

    public SqlLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Link add(String url) {
        Long id = jdbcTemplate.queryForObject("""
            insert into links(url)
            values (?)
            returning id
            """, Long.class, url);

        if (id == null) {
            throw new IllegalStateException("Failed to insert link: id is null");
        }

        return findById(id).orElseThrow(() -> new IllegalStateException("Inserted link not found: id=" + id));
    }

    @Override
    public Optional<Link> findByUrl(String url) {
        return jdbcTemplate.query("""
            select id, url, last_updated_at, created_at
            from links
            where url = ?
            """, linkRowMapper(), url).stream().findFirst();
    }

    @Override
    public void remove(long id) {
        jdbcTemplate.update("delete from links where id = ?", id);
    }

    @Override
    public Optional<Link> findById(long id) {
        return jdbcTemplate.query("""
            select id, url, last_updated_at, created_at
            from links
            where id = ?
            """, linkRowMapper(), id).stream().findFirst();
    }

    private RowMapper<Link> linkRowMapper() {
        return new RowMapper<>() {
            @Override
            public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Link(
                        rs.getLong("id"),
                        rs.getString("url"),
                        rs.getTimestamp("last_updated_at") == null
                                ? null
                                : rs.getTimestamp("last_updated_at").toInstant(),
                        rs.getTimestamp("created_at").toInstant());
            }
        };
    }

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.query("""
            select id, url, last_updated_at, created_at
            from links
            order by id
            """, linkRowMapper());
    }

    @Override
    public List<Link> findBatch(int limit, int offset) {
        return jdbcTemplate.query("""
            select id, url, last_updated_at, created_at
            from links
            order by id
            limit ? offset ?
            """, linkRowMapper(), limit, offset);
    }

    @Override
    public void updateLastUpdatedAt(long id, Instant updatedAt) {
        jdbcTemplate.update("""
            update links
            set last_updated_at = ?
            where id = ?
            """, Timestamp.from(updatedAt), id);
    }
}
