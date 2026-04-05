package backend.academy.linktracker.scrapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.schedule.LinkUpdaterScheduler;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdateReport;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdaterService;
import org.junit.jupiter.api.Test;
import java.util.List;

public class ScheduleTest {

    @Test
    void shouldDelegateToLinkUpdaterService() {
        LinkUpdaterService linkUpdaterService = mock(LinkUpdaterService.class);

        when(linkUpdaterService.update())
            .thenReturn(new LinkUpdateReport(0, 0, List.of()));

        LinkUpdaterScheduler scheduler = new LinkUpdaterScheduler(linkUpdaterService);

        scheduler.update();

        verify(linkUpdaterService).update();
    }
}
