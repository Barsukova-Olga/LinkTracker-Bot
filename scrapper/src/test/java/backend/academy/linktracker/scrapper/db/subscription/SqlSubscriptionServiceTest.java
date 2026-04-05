package backend.academy.linktracker.scrapper.db.subscription;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.linktracker.scrapper.ScrapperApplication;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.LinkTagRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestPropertySource(properties = {"app.database.access-type=SQL", "app.schedule.enabled=false"})
class SqlSubscriptionServiceTest extends SubscriptionServiceTest {

    @Autowired
    protected LinkTagRepository linkTagRepository;

    @Test
    void shouldRemoveTagsOnUnsubscribe() {
        long chatId = 5L;
        String url = "https://example.com/with-tags";

        Link link = subscriptionService.subscribe(chatId, url, List.of("work", "urgent"));

        assertEquals(List.of("urgent", "work"), linkTagRepository.findTags(chatId, link.id()));

        subscriptionService.unsubscribe(chatId, url);

        assertEquals(List.of(), linkTagRepository.findTags(chatId, link.id()));
        assertEquals(List.of(), subscriptionService.getLinks(chatId));
    }

    @Test
    void shouldSubscribeAndSaveTags() {
        long chatId = 1L;
        String url = "https://github.com/openai/openai-java";

        Link link = subscriptionService.subscribe(chatId, url, List.of("work", "backend"));

        List<Link> links = subscriptionService.getLinks(chatId);
        List<String> tags = linkTagRepository.findTags(chatId, link.id());

        assertEquals(1, links.size());
        assertEquals(url, links.get(0).url());
        assertEquals(List.of("backend", "work"), tags);
    }
}
