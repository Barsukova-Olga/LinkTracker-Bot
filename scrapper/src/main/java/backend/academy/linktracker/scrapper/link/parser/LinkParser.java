package backend.academy.linktracker.scrapper.link.parser;

import backend.academy.linktracker.scrapper.model.ParsedLink;
import java.util.Optional;

public interface LinkParser {
    Optional<ParsedLink> parse(String link);
}
