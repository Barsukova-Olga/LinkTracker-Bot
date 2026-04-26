package backend.academy.linktracker.scrapper.db;

import backend.academy.linktracker.scrapper.ScrapperApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = ScrapperApplication.class)
public abstract class AbstractPostgresSpringBootTest {

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
        registry.add("spring.liquibase.change-log", () -> "migrations/master.xml");

        registry.add("app.bot.base-url", () -> "http://localhost:8080");
        registry.add("app.schedule.enabled", () -> "false");

        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
        registry.add("app.kafka.topics.link-updates", () -> "link-updates");

        registry.add("app.outbox.enabled", () -> "false");
        registry.add("app.outbox.batch-size", () -> "10");
        registry.add("app.outbox.max-attempts", () -> "3");
        registry.add("app.outbox.poll-interval", () -> "10s");

        registry.add(
                "spring.kafka.producer.key-serializer", () -> "org.apache.kafka.common.serialization.LongSerializer");
        registry.add(
                "spring.kafka.producer.value-serializer",
                () -> "org.springframework.kafka.support.serializer.JsonSerializer");
        registry.add("spring.kafka.producer.properties.spring.json.add.type.headers", () -> "false");
    }
}
