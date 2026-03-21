package backend.academy.linktracker.scrapper.repository.orm;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.orm.entity.LinkEntity;
import backend.academy.linktracker.scrapper.orm.repository.LinkJpaRepository;
import backend.academy.linktracker.scrapper.repository.LinkRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "ORM")
@Transactional
public class OrmLinkRepository implements LinkRepository {

    private final LinkJpaRepository linkJpaRepository;

    public OrmLinkRepository(LinkJpaRepository linkJpaRepository) {
        this.linkJpaRepository = linkJpaRepository;
    }

    @Override
    public Link add(String url) {
        LinkEntity entity = new LinkEntity();
        entity.setUrl(url);
        entity.setCreatedAt(Instant.now());

        LinkEntity saved = linkJpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<Link> findByUrl(String url) {
        return linkJpaRepository.findByUrl(url).map(this::toModel);
    }

    @Override
    public Optional<Link> findById(long id) {
        return linkJpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<Link> findAll() {
        return linkJpaRepository.findAll().stream().map(this::toModel).toList();
    }

    @Override
    public void updateLastUpdatedAt(long id, Instant updatedAt) {
        linkJpaRepository.findById(id).ifPresent(entity -> {
            entity.setLastUpdatedAt(updatedAt);
            linkJpaRepository.save(entity);
        });
    }

    @Override
    public void remove(long id) {
        linkJpaRepository.deleteById(id);
    }

    private Link toModel(LinkEntity entity) {
        return new Link(entity.getId(), entity.getUrl(), entity.getLastUpdatedAt(), entity.getCreatedAt());
    }
}
