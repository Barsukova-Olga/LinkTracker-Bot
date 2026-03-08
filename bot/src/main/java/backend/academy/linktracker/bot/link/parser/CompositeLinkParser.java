package backend.academy.linktracker.bot.link.parser;

import backend.academy.linktracker.bot.link.TrackedLink;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CompositeLinkParser implements LinkParser {
    private final List<SpecLinkParser> parsers;

    public CompositeLinkParser(List<SpecLinkParser> parsers) {
        this.parsers = parsers;
    }

    @Override
    public Optional<TrackedLink> parse(String link) {
        if (link == null || link.isBlank()) {
            return Optional.empty();
        }

        try {
            URI uri = new URI(link.trim());
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
                return Optional.empty();
            }

            for (SpecLinkParser parser : parsers) {
                Optional<TrackedLink> result = parser.parse(uri);
                if (result.isPresent()) {
                    return result;
                }
            }
            return Optional.empty();
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }
}
