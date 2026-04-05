package backend.academy.linktracker.scrapper.repository;

import backend.academy.linktracker.scrapper.model.Link;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {
    Link add(String url);

    Optional<Link> findByUrl(String url);

    void remove(long id);

    Optional<Link> findById(long id);

    List<Link> findAll();

    List<Link> findBatch(int limit, int offset);

    void updateLastUpdatedAt(long id, Instant updatedAt);
}
