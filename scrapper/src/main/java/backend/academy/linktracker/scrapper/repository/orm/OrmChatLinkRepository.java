package backend.academy.linktracker.scrapper.repository.orm;

import backend.academy.linktracker.scrapper.orm.entity.ChatLinkEntity;
import backend.academy.linktracker.scrapper.orm.entity.ChatLinkId;
import backend.academy.linktracker.scrapper.orm.repository.ChatLinkJpaRepository;
import backend.academy.linktracker.scrapper.repository.ChatLinkRepository;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "ORM")
@Transactional
public class OrmChatLinkRepository implements ChatLinkRepository {

    private final ChatLinkJpaRepository chatLinkJpaRepository;

    public OrmChatLinkRepository(ChatLinkJpaRepository chatLinkJpaRepository) {
        this.chatLinkJpaRepository = chatLinkJpaRepository;
    }

    @Override
    public void add(long chatId, long linkId) {
        ChatLinkId id = new ChatLinkId(chatId, linkId);
        if (!chatLinkJpaRepository.existsById(id)) {
            chatLinkJpaRepository.save(new ChatLinkEntity(chatId, linkId));
        }
    }

    @Override
    public void remove(long chatId, long linkId) {
        chatLinkJpaRepository.deleteById(new ChatLinkId(chatId, linkId));
    }

    @Override
    public List<Long> findLinksByChatId(long chatId) {
        return chatLinkJpaRepository.findByChatId(chatId).stream()
                .map(ChatLinkEntity::getLinkId)
                .toList();
    }

    @Override
    public List<Long> findChatsByLinkId(long linkId) {
        return chatLinkJpaRepository.findByLinkId(linkId).stream()
                .map(ChatLinkEntity::getChatId)
                .toList();
    }
}
