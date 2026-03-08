package backend.academy.linktracker.scrapper.exception;

import java.net.URI;

public class InvalidLinkException extends RuntimeException {
    public InvalidLinkException(URI link) {
        super("Unsupported or invalid link: " + link);
    }
}
