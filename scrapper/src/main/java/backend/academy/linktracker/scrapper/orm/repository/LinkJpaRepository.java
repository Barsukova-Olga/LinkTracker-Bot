package backend.academy.linktracker.scrapper.orm.repository;

import backend.academy.linktracker.scrapper.orm.entity.LinkEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkJpaRepository extends JpaRepository<LinkEntity, Long> {
    Optional<LinkEntity> findByUrl(String url);
}
