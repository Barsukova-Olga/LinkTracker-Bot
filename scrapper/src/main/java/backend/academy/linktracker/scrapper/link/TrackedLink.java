package backend.academy.linktracker.scrapper.link;

import lombok.Getter;

@Getter
public class TrackedLink {
    private final LinkType linkType;
    private final String url;

    public TrackedLink(LinkType linkType, String url) {
        this.linkType = linkType;
        this.url = url;
    }
}
