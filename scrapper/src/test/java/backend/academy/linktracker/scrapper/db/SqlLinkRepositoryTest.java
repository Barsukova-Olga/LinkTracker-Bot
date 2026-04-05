package backend.academy.linktracker.scrapper.db;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.linktracker.scrapper.ScrapperApplication;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.LinkRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = ScrapperApplication.class)
class SqlLinkRepositoryTest extends AbstractPostgresSpringBootTest {

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
    private LinkRepository linkRepository;

    @Test
    void shouldAddAndFindByUrl() {
        String url = "https://github.com/openai/openai-java";

        Link saved = linkRepository.add(url);
        Optional<Link> found = linkRepository.findByUrl(url);

        assertNotNull(saved);
        assertTrue(saved.id() > 0);
        assertEquals(url, saved.url());

        assertTrue(found.isPresent());
        assertEquals(saved.id(), found.orElseThrow().id());
        assertEquals(url, found.orElseThrow().url());
    }

    @Test
    void shouldRemoveLink() {
        String url = "https://github.com/spring-projects/spring-boot";

        Link saved = linkRepository.add(url);
        linkRepository.remove(saved.id());

        Optional<Link> found = linkRepository.findByUrl(url);

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenLinkDoesNotExist() {
        Optional<Link> found = linkRepository.findByUrl("https://example.com/not-found");

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFailWhenAddingDuplicateUrl() {
        String url = "https://github.com/duplicate/repo";

        linkRepository.add(url);

        assertThrows(DataAccessException.class, () -> linkRepository.add(url));
    }
}
