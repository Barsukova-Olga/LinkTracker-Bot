package backend.academy.linktracker.scrapper.repository;

import backend.academy.linktracker.scrapper.model.ParsedLink;
import backend.academy.linktracker.scrapper.model.TrackedParsedLink;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface ScrapperRepository {

    void registerChat(long chatId);

    void deleteChat(long chatId);

    boolean chatExists(long chatId);

    List<TrackedParsedLink> getLinks(long chatId);

    TrackedParsedLink addLink(long chatId, ParsedLink parsedLink, List<String> tags, List<String> filters);

    TrackedParsedLink removeLink(long chatId, URI url);

    Map<Long, List<TrackedParsedLink>> findAll();

    void updateLastUpdatedAt(long chatId, URI url, Instant lastUpdatedAt);
}
