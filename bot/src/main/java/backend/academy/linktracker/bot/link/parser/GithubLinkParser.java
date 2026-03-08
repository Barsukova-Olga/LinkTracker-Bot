package backend.academy.linktracker.bot.link.parser;

import backend.academy.linktracker.bot.link.LinkType;
import backend.academy.linktracker.bot.link.TrackedLink;
import java.net.URI;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GithubLinkParser implements SpecLinkParser {
    @Override
    public Optional<TrackedLink> parse(URI uri) {
        String host = uri.getHost();
        String path = uri.getPath();

        if (host == null || path == null) {
            return Optional.empty();
        }

        host = host.toLowerCase();

        if (!host.equals("github.com")) {
            return Optional.empty();
        }

        String[] parts = path.split("/");

        if (parts.length >= 3 && !parts[1].isBlank() && !parts[2].isBlank()) {
            URI normalizedUri = URI.create("https://github.com/" + parts[1] + "/" + parts[2]);
            return Optional.of(new TrackedLink(LinkType.GITHUB, normalizedUri));
        }

        return Optional.empty();
    }
}
