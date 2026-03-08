package backend.academy.linktracker.scrapper.link.parser;

import backend.academy.linktracker.scrapper.model.GithubParsedLink;
import backend.academy.linktracker.scrapper.model.ParsedLink;
import java.net.URI;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GithubLinkParser implements SpecLinkParser {
    @Override
    public Optional<ParsedLink> parse(URI uri) {
        String host = uri.getHost();
        if (host == null || !(host.equals("github.com") || host.equals("www.github.com"))) {
            return Optional.empty();
        }

        String[] parts = uri.getPath().split("/");
        if (parts.length < 3 || parts[1].isBlank() || parts[2].isBlank()) {
            return Optional.empty();
        }

        return Optional.of(new GithubParsedLink(uri, parts[1], parts[2]));
    }
}
