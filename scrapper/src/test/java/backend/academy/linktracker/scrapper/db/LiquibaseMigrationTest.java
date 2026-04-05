package backend.academy.linktracker.scrapper.db;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.linktracker.scrapper.ScrapperApplication;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(
        classes = {
            ScrapperApplication.class,
        },
        properties = {"spring.liquibase.enabled=true", "spring.liquibase.change-log=classpath:/migrations/master.xml"})
class LiquibaseMigrationTest {
    @Autowired
    private ApplicationContext applicationContext;

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
    }

    @Test
    void dataSourceBeanShouldExist() {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        assertNotNull(dataSource);
    }

    @Test
    void liquibaseBeanShouldExist() {
        SpringLiquibase liquibase = applicationContext.getBean(SpringLiquibase.class);
        assertNotNull(liquibase);
    }

    @Test
    void migrationsShouldCreateAllTables() throws Exception {
        try (Connection connection =
                DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {

            assertTrue(tableExists(connection, "chats"));
            assertTrue(tableExists(connection, "links"));
            assertTrue(tableExists(connection, "chat_link"));
            assertTrue(tableExists(connection, "link_tag"));
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws Exception {
        String sql = """
        select count(*)
        from information_schema.tables
        where table_name = ? and table_schema = 'public'
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tableName);

            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}
