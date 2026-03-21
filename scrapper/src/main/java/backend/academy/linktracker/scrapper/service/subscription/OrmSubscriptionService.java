package backend.academy.linktracker.scrapper.service.subscription;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.orm.entity.ChatEntity;
import backend.academy.linktracker.scrapper.orm.entity.ChatLinkEntity;
import backend.academy.linktracker.scrapper.orm.entity.ChatLinkId;
import backend.academy.linktracker.scrapper.orm.entity.LinkEntity;
import backend.academy.linktracker.scrapper.orm.entity.LinkTagEntity;
import backend.academy.linktracker.scrapper.orm.entity.LinkTagId;
import backend.academy.linktracker.scrapper.orm.repository.ChatJpaRepository;
import backend.academy.linktracker.scrapper.orm.repository.ChatLinkJpaRepository;
import backend.academy.linktracker.scrapper.orm.repository.LinkJpaRepository;
import backend.academy.linktracker.scrapper.orm.repository.LinkTagJpaRepository;
import backend.academy.linktracker.scrapper.service.SubscriptionService;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "ORM")
public class OrmSubscriptionService implements SubscriptionService {

    private final ChatJpaRepository chatRepo;
    private final LinkJpaRepository linkRepo;
    private final ChatLinkJpaRepository chatLinkRepo;
    private final LinkTagJpaRepository linkTagRepo;

    public OrmSubscriptionService(
            ChatJpaRepository chatRepo,
            LinkJpaRepository linkRepo,
            ChatLinkJpaRepository chatLinkRepo,
            LinkTagJpaRepository linkTagRepo) {
        this.chatRepo = chatRepo;
        this.linkRepo = linkRepo;
        this.chatLinkRepo = chatLinkRepo;
        this.linkTagRepo = linkTagRepo;
    }

    @Override
    @Transactional
    public Link subscribe(long chatId, String url, List<String> tags) {
        if (!chatRepo.existsById(chatId)) {
            chatRepo.save(new ChatEntity(chatId));
        }

        LinkEntity link = linkRepo.findByUrl(url).orElseGet(() -> linkRepo.save(new LinkEntity(url)));

        ChatLinkId chatLinkId = new ChatLinkId(chatId, link.getId());
        if (!chatLinkRepo.existsById(chatLinkId)) {
            chatLinkRepo.save(new ChatLinkEntity(chatId, link.getId()));
            if (tags != null) {
                for (String tag : tags) {
                    if (tag != null && !tag.isBlank()) {
                        LinkTagId tagId = new LinkTagId(chatId, link.getId(), tag.trim());
                        if (!linkTagRepo.existsById(tagId)) {
                            linkTagRepo.save(new LinkTagEntity(chatId, link.getId(), tag.trim()));
                        }
                    }
                }
            }
        }

        return toModel(link);
    }

    @Override
    @Transactional
    public void unsubscribe(long chatId, String url) {
        linkRepo.findByUrl(url).ifPresent(link -> {
            ChatLinkId chatLinkId = new ChatLinkId(chatId, link.getId());

            if (chatLinkRepo.existsById(chatLinkId)) {
                linkTagRepo.deleteByChatIdAndLinkId(chatId, link.getId());
                chatLinkRepo.deleteById(chatLinkId);
            }

            if (chatLinkRepo.findByLinkId(link.getId()).isEmpty()) {
                linkRepo.deleteById(link.getId());
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Link> getLinks(long chatId) {
        return chatLinkRepo.findByChatId(chatId).stream()
                .map(chatLink -> linkRepo.findById(chatLink.getLinkId()).orElseThrow())
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Link> getLinks(long chatId, String tag) {
        if (tag == null || tag.isBlank()) {
            return getLinks(chatId);
        }

        return linkTagRepo.findByChatIdAndTag(chatId, tag).stream()
                .map(tagEntity -> linkRepo.findById(tagEntity.getLinkId()).orElseThrow())
                .map(this::toModel)
                .toList();
    }

    private Link toModel(LinkEntity entity) {
        return new Link(entity.getId(), entity.getUrl(), entity.getLastUpdatedAt(), entity.getCreatedAt());
    }
}
