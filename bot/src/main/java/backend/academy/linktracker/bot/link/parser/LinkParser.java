package backend.academy.linktracker.bot.link.parser;

import backend.academy.linktracker.bot.link.TrackedLink;
import java.util.Optional;

public interface LinkParser {
    Optional<TrackedLink> parse(String link);
}
