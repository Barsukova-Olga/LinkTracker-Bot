package backend.academy.linktracker.scrapper.schedule;

import backend.academy.linktracker.scrapper.model.TrackedParsedLink;
import java.time.Instant;
import java.util.Optional;

public interface LinkUpdateChecker {
    Optional<Instant> getCurrentLastUpdatedAt(TrackedParsedLink link);
}
