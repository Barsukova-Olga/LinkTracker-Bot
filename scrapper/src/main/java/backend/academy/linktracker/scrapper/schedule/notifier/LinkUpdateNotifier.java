package backend.academy.linktracker.scrapper.schedule.notifier;

import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.model.TrackedParsedLink;

public interface LinkUpdateNotifier {
    void notifyUpdate(LinkUpdateRequest update);
}
