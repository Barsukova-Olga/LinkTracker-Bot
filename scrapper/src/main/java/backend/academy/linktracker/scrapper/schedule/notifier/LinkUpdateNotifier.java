package backend.academy.linktracker.scrapper.schedule.notifier;

import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;

public interface LinkUpdateNotifier {
    void notifyUpdate(LinkUpdateRequest update);
}
