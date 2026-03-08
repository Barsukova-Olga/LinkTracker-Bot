package backend.academy.linktracker.scrapper.link.parser;

import backend.academy.linktracker.scrapper.model.ParsedLink;
import java.net.URI;
import java.util.Optional;

public interface SpecLinkParser {
    Optional<ParsedLink> parse(URI uri);
}
