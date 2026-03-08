package backend.academy.linktracker.scrapper.exception;

import java.net.URI;

public class LinkAlreadyTrackedException extends RuntimeException {
    public LinkAlreadyTrackedException(URI link) {
        super("Link already tracked: " + link);
    }
}
