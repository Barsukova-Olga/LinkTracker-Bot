package backend.academy.linktracker.bot.link;

import java.net.URI;
import lombok.Getter;

@Getter
public class TrackedLink {
    private final LinkType linkType;
    private final URI url;

    public TrackedLink(LinkType linkType, URI url) {
        this.linkType = linkType;
        this.url = url;
    }
}
