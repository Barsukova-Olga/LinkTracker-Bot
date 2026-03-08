package backend.academy.linktracker.scrapper.model;

import java.net.URI;

public record GithubParsedLink(URI uri, String owner, String repo) implements ParsedLink {}
