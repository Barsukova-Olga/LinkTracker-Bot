package backend.academy.linktracker.scrapper;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.linktracker.scrapper.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = ScrapperApplication.class)
class SqlChatRepositoryTest extends AbstractPostgresSpringBootTest {

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

        // важно: включаем liquibase
        registry.add("spring.liquibase.enabled", () -> true);
        registry.add("spring.liquibase.change-log", () -> "classpath:/migrations/master.xml");
    }

    @Autowired
    private ChatRepository chatRepository;

    @Test
    void shouldAddAndCheckExists() {
        long chatId = 123L;

        chatRepository.add(chatId);

        assertTrue(chatRepository.exists(chatId));
    }

    @Test
    void shouldRemoveChat() {
        long chatId = 456L;

        chatRepository.add(chatId);
        chatRepository.remove(chatId);

        assertFalse(chatRepository.exists(chatId));
    }

    @Test
    void shouldReturnFalseWhenChatDoesNotExist() {
        assertFalse(chatRepository.exists(999L));
    }

    @Test
    void shouldFailWhenAddingDuplicateChat() {
        long chatId = 777L;

        chatRepository.add(chatId);

        assertThrows(Exception.class, () -> chatRepository.add(chatId));
    }
}
