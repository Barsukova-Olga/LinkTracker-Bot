package backend.academy.linktracker.bot.link.parser;

import backend.academy.linktracker.bot.link.TrackedLink;
import java.net.URI;
import java.util.Optional;

public interface SpecLinkParser {
    Optional<TrackedLink> parse(URI uri);
}
