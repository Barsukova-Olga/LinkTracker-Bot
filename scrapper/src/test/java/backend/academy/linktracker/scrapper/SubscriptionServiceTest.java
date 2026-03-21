package backend.academy.linktracker.scrapper;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.LinkTagRepository;
import backend.academy.linktracker.scrapper.service.SubscriptionService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = ScrapperApplication.class, properties = "app.database.access-type=SQL")
class SubscriptionServiceTest extends AbstractPostgresSpringBootTest {

    @Autowired
    Environment env;

    @Test
    void debug() {
        System.out.println(env.getProperty("app.database.access-type"));
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("delete from link_tag");
        jdbcTemplate.execute("delete from chat_link");
        jdbcTemplate.execute("delete from links");
        jdbcTemplate.execute("delete from chats");
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.liquibase.enabled", () -> true);
        registry.add("spring.liquibase.change-log", () -> "classpath:/migrations/master.xml");
    }

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private LinkTagRepository linkTagRepository;

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

        // 🔥 ключевая проверка — это одна и та же ссылка
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
