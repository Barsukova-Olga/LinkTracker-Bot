package backend.academy.linktracker.scrapper.service.subscription;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.ChatLinkRepository;
import backend.academy.linktracker.scrapper.repository.ChatRepository;
import backend.academy.linktracker.scrapper.repository.LinkRepository;
import backend.academy.linktracker.scrapper.repository.LinkTagRepository;
import backend.academy.linktracker.scrapper.service.SubscriptionService;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "SQL")
public class SqlSubscriptionService implements SubscriptionService {

    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;
    private final LinkTagRepository linkTagRepository;

    public SqlSubscriptionService(
            ChatRepository chatRepository,
            LinkRepository linkRepository,
            ChatLinkRepository chatLinkRepository,
            LinkTagRepository linkTagRepository) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.chatLinkRepository = chatLinkRepository;
        this.linkTagRepository = linkTagRepository;
    }

    @Override
    @Transactional
    public Link subscribe(long chatId, String url, List<String> tags) {
        if (!chatRepository.exists(chatId)) {
            chatRepository.add(chatId);
        }

        Link link = linkRepository.findByUrl(url).orElseGet(() -> linkRepository.add(url));

        chatLinkRepository.add(chatId, link.id());

        if (tags != null) {
            for (String tag : tags) {
                if (tag != null && !tag.isBlank()) {
                    linkTagRepository.addTag(chatId, link.id(), tag.trim());
                }
            }
        }

        return link;
    }

    @Override
    @Transactional
    public void unsubscribe(long chatId, String url) {
        Optional<Link> linkOpt = linkRepository.findByUrl(url);

        if (linkOpt.isEmpty()) {
            return;
        }

        Link link = linkOpt.orElseThrow();

        linkTagRepository.removeAllTags(chatId, link.id());
        chatLinkRepository.remove(chatId, link.id());

        List<Long> chats = chatLinkRepository.findChatsByLinkId(link.id());

        if (chats.isEmpty()) {
            linkRepository.remove(link.id());
        }
    }

    @Override
    public List<Link> getLinks(long chatId) {
        List<Long> linkIds = chatLinkRepository.findLinksByChatId(chatId);

        return linkIds.stream()
                .map(id -> linkRepository.findById(id).orElseThrow())
                .toList();
    }

    @Override
    public List<Link> getLinks(long chatId, String tag) {
        if (tag == null || tag.isBlank()) {
            return getLinks(chatId);
        }

        return linkTagRepository.findLinksByTag(chatId, tag);
    }
}
