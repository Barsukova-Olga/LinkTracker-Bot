package backend.academy.linktracker.scrapper;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import backend.academy.linktracker.scrapper.service.SubscriptionService;
import backend.academy.linktracker.scrapper.service.subscription.SqlSubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.database.access-type=SQL")
class SqlAccessTypeTest extends AbstractPostgresSpringBootTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    void shouldLoadSqlSubscriptionService() {
        assertInstanceOf(SqlSubscriptionService.class, subscriptionService);
    }
}
