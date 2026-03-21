package backend.academy.linktracker.scrapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import backend.academy.linktracker.scrapper.schedule.LinkUpdaterScheduler;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdaterService;
import org.junit.jupiter.api.Test;

public class ScheduleTest {

    @Test
    void shouldDelegateToLinkUpdaterService() {
        LinkUpdaterService linkUpdaterService = mock(LinkUpdaterService.class);

        LinkUpdaterScheduler scheduler = new LinkUpdaterScheduler(linkUpdaterService);

        scheduler.update();

        verify(linkUpdaterService).update();
    }
}
