package backend.academy.linktracker.scrapper.repository.orm;

import backend.academy.linktracker.scrapper.orm.entity.ChatEntity;
import backend.academy.linktracker.scrapper.orm.repository.ChatJpaRepository;
import backend.academy.linktracker.scrapper.repository.ChatRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "ORM")
@Transactional
public class OrmChatRepository implements ChatRepository {

    private final ChatJpaRepository chatJpaRepository;

    public OrmChatRepository(ChatJpaRepository chatJpaRepository) {
        this.chatJpaRepository = chatJpaRepository;
    }

    @Override
    public void add(long chatId) {
        if (!chatJpaRepository.existsById(chatId)) {
            chatJpaRepository.save(new ChatEntity(chatId));
        }
    }

    @Override
    public void remove(long chatId) {
        chatJpaRepository.deleteById(chatId);
    }

    @Override
    public boolean exists(long chatId) {
        return chatJpaRepository.existsById(chatId);
    }
}
