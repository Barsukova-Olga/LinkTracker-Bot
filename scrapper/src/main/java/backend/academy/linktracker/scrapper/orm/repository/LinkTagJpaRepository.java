package backend.academy.linktracker.scrapper.orm.repository;

import backend.academy.linktracker.scrapper.orm.entity.LinkTagEntity;
import backend.academy.linktracker.scrapper.orm.entity.LinkTagId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkTagJpaRepository extends JpaRepository<LinkTagEntity, LinkTagId> {

    List<LinkTagEntity> findByChatIdAndLinkId(Long chatId, Long linkId);

    List<LinkTagEntity> findByChatIdAndTag(Long chatId, String tag);

    void deleteByChatIdAndLinkId(Long chatId, Long linkId);
}
