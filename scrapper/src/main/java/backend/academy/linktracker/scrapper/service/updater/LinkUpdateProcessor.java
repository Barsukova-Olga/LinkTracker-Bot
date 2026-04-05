package backend.academy.linktracker.scrapper.service.updater;

import backend.academy.linktracker.scrapper.model.Link;
import java.util.Optional;

public interface LinkUpdateProcessor {
    Optional<LinkUpdateEvent> process(Link link);
}
