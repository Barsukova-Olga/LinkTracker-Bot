package backend.academy.linktracker.scrapper.db.subscription;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.database.access-type=ORM", "app.schedule.enabled=false"})
class OrmSubscriptionServiceTest extends SubscriptionServiceTest {}
