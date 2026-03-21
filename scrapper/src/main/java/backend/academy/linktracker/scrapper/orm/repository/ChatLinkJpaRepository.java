package backend.academy.linktracker.scrapper.orm.repository;

import backend.academy.linktracker.scrapper.orm.entity.ChatLinkEntity;
import backend.academy.linktracker.scrapper.orm.entity.ChatLinkId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLinkJpaRepository extends JpaRepository<ChatLinkEntity, ChatLinkId> {

    List<ChatLinkEntity> findByChatId(Long chatId);

    List<ChatLinkEntity> findByLinkId(Long linkId);
}
