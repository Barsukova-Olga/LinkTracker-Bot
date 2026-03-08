package backend.academy.linktracker.scrapper.model;

import java.net.URI;

public sealed interface ParsedLink permits GithubParsedLink, StackoverflowParsedLink {

    URI uri();
}
