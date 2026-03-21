package backend.academy.linktracker.scrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.service.SubscriptionService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.database.access-type=ORM")
class OrmSubscriptionServiceTest extends AbstractPostgresSpringBootTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("delete from link_tag");
        jdbcTemplate.execute("delete from chat_link");
        jdbcTemplate.execute("delete from links");
        jdbcTemplate.execute("delete from chats");
    }

    @Test
    void shouldSubscribeAndGetLinksByTag() {
        long chatId = 1L;

        subscriptionService.subscribe(chatId, "https://github.com/openai/openai-java", List.of("work", "backend"));

        subscriptionService.subscribe(chatId, "https://stackoverflow.com/questions/1/test", List.of("study"));

        List<Link> allLinks = subscriptionService.getLinks(chatId);
        List<Link> workLinks = subscriptionService.getLinks(chatId, "work");
        List<Link> studyLinks = subscriptionService.getLinks(chatId, "study");

        assertEquals(2, allLinks.size());
        assertEquals(1, workLinks.size());
        assertEquals(1, studyLinks.size());

        assertEquals("https://github.com/openai/openai-java", workLinks.get(0).url());
        assertEquals(
                "https://stackoverflow.com/questions/1/test", studyLinks.get(0).url());
    }

    @Test
    void shouldUnsubscribeAndRemoveLastLink() {
        long chatId = 2L;
        String url = "https://example.com/remove-me";

        subscriptionService.subscribe(chatId, url, List.of("work", "urgent"));

        assertEquals(1, subscriptionService.getLinks(chatId).size());

        subscriptionService.unsubscribe(chatId, url);

        List<Link> links = subscriptionService.getLinks(chatId);
        List<Link> workLinks = subscriptionService.getLinks(chatId, "work");

        assertEquals(0, links.size());
        assertEquals(0, workLinks.size());
    }

    @Test
    void shouldNotRemoveLinkIfOtherSubscriberExists() {
        String url = "https://example.com/shared-link";

        subscriptionService.subscribe(10L, url, List.of("work"));
        subscriptionService.subscribe(20L, url, List.of("study"));

        subscriptionService.unsubscribe(10L, url);

        List<Link> linksFirst = subscriptionService.getLinks(10L);
        List<Link> linksSecond = subscriptionService.getLinks(20L);
        List<Link> studyLinksSecond = subscriptionService.getLinks(20L, "study");

        assertEquals(0, linksFirst.size());
        assertEquals(1, linksSecond.size());
        assertEquals(1, studyLinksSecond.size());
        assertEquals(url, linksSecond.get(0).url());
    }
}
