package backend.academy.linktracker.scrapper.db.subscription;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.linktracker.scrapper.ScrapperApplication;
import backend.academy.linktracker.scrapper.db.AbstractPostgresSpringBootTest;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.service.SubscriptionService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

public abstract class SubscriptionServiceTest extends AbstractPostgresSpringBootTest {
    @Autowired
    protected SubscriptionService subscriptionService;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("delete from link_tag");
        jdbcTemplate.execute("delete from chat_link");
        jdbcTemplate.execute("delete from links");
        jdbcTemplate.execute("delete from chats");
    }

    @Test
    void shouldSubscribeAndGetLinks() {
        long chatId = 1L;
        String url = "https://github.com/openai/openai-java";

        subscriptionService.subscribe(chatId, url, List.of());

        List<Link> links = subscriptionService.getLinks(chatId);

        assertEquals(1, links.size());
        assertEquals(url, links.get(0).url());
    }

    @Test
    void shouldNotDuplicateLinkOnMultipleSubscriptions() {
        long chatId1 = 1L;
        long chatId2 = 2L;
        String url = "https://github.com/spring-projects/spring-boot";

        subscriptionService.subscribe(chatId1, url, List.of());
        subscriptionService.subscribe(chatId2, url, List.of());

        List<Link> links1 = subscriptionService.getLinks(chatId1);
        List<Link> links2 = subscriptionService.getLinks(chatId2);

        assertEquals(1, links1.size());
        assertEquals(1, links2.size());

        assertEquals(links1.get(0).id(), links2.get(0).id());
    }

    @Test
    void shouldUnsubscribeAndRemoveLinkIfLastSubscriber() {
        long chatId = 3L;
        String url = "https://example.com/delete-me";

        subscriptionService.subscribe(chatId, url, List.of());
        subscriptionService.unsubscribe(chatId, url);

        List<Link> links = subscriptionService.getLinks(chatId);

        assertTrue(links.isEmpty());
    }

    @Test
    void shouldNotRemoveLinkIfOtherSubscribersExist() {
        long chatId1 = 10L;
        long chatId2 = 20L;
        String url = "https://example.com/shared";

        subscriptionService.subscribe(chatId1, url, List.of());
        subscriptionService.subscribe(chatId2, url, List.of());

        subscriptionService.unsubscribe(chatId1, url);

        List<Link> links2 = subscriptionService.getLinks(chatId2);

        assertEquals(1, links2.size());
        assertEquals(url, links2.get(0).url());
    }

    @Test
    void shouldHandleUnsubscribeFromNonExistingLink() {
        long chatId = 100L;

        assertDoesNotThrow(() -> subscriptionService.unsubscribe(chatId, "https://not-exists.com"));
    }

    @Test
    void shouldFilterLinksByTag() {
        long chatId = 10L;

        subscriptionService.subscribe(chatId, "https://a.com", List.of("work"));
        subscriptionService.subscribe(chatId, "https://b.com", List.of("study"));
        subscriptionService.subscribe(chatId, "https://c.com", List.of("work"));

        List<Link> workLinks = subscriptionService.getLinks(chatId, "work");
        List<Link> studyLinks = subscriptionService.getLinks(chatId, "study");

        assertEquals(2, workLinks.size());
        assertEquals(1, studyLinks.size());

        assertEquals("https://a.com", workLinks.get(0).url());
        assertEquals("https://c.com", workLinks.get(1).url());
    }
}
