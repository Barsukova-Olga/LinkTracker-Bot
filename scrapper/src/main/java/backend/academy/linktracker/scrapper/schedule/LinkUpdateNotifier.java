package backend.academy.linktracker.scrapper.schedule;

import backend.academy.linktracker.scrapper.model.TrackedParsedLink;

public interface LinkUpdateNotifier {
    void notifyUpdate(long chatId, TrackedParsedLink link);
}
