package backend.academy.linktracker.scrapper.db;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import backend.academy.linktracker.scrapper.service.SubscriptionService;
import backend.academy.linktracker.scrapper.service.subscription.OrmSubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.database.access-type=ORM")
class OrmAccessTypeTest extends AbstractPostgresSpringBootTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    void shouldLoadOrmSubscriptionService() {
        assertInstanceOf(OrmSubscriptionService.class, subscriptionService);
    }
}
