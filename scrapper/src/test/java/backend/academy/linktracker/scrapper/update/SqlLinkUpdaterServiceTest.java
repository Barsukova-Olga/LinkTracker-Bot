package backend.academy.linktracker.scrapper.update;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.database.access-type=SQL")
class SqlLinkUpdaterServiceTest extends LinkUpdaterServiceTest {}
