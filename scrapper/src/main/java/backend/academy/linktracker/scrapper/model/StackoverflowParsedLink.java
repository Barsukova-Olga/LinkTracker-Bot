package backend.academy.linktracker.scrapper.model;

import java.net.URI;

public record StackoverflowParsedLink(URI uri, long questionId) implements ParsedLink {}
