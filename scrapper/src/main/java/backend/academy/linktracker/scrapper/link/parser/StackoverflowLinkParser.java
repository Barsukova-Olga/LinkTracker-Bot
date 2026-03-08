package backend.academy.linktracker.scrapper.link.parser;

import backend.academy.linktracker.scrapper.model.ParsedLink;
import backend.academy.linktracker.scrapper.model.StackoverflowParsedLink;
import java.net.URI;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class StackoverflowLinkParser implements SpecLinkParser {

    @Override
    public Optional<ParsedLink> parse(URI uri) {
        String host = uri.getHost();
        if (host == null || !(host.equals("stackoverflow.com") || host.equals("www.stackoverflow.com"))) {
            return Optional.empty();
        }

        String[] parts = uri.getPath().split("/");
        if (parts.length < 3 || !"questions".equals(parts[1])) {
            return Optional.empty();
        }

        try {
            long questionId = Long.parseLong(parts[2]);
            return Optional.of(new StackoverflowParsedLink(uri, questionId));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
