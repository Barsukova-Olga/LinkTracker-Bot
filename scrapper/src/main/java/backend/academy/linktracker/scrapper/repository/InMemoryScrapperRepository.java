package backend.academy.linktracker.scrapper.repository;

import backend.academy.linktracker.scrapper.model.ParsedLink;
import backend.academy.linktracker.scrapper.model.TrackedParsedLink;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryScrapperRepository implements ScrapperRepository {

    private final Map<Long, Map<String, TrackedParsedLink>> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void registerChat(long chatId) {
        storage.putIfAbsent(chatId, new ConcurrentHashMap<>());
    }

    @Override
    public void deleteChat(long chatId) {
        storage.remove(chatId);
    }

    @Override
    public boolean chatExists(long chatId) {
        return storage.containsKey(chatId);
    }

    @Override
    public List<TrackedParsedLink> getLinks(long chatId) {
        Map<String, TrackedParsedLink> links = storage.get(chatId);
        if (links == null) {
            return List.of();
        }
        return new ArrayList<>(links.values());
    }

    @Override
    public TrackedParsedLink addLink(long chatId, ParsedLink parsedLink, List<String> tags, List<String> filters) {
        storage.putIfAbsent(chatId, new ConcurrentHashMap<>());

        Map<String, TrackedParsedLink> links = storage.get(chatId);
        TrackedParsedLink trackedLink = new TrackedParsedLink(
                idGenerator.getAndIncrement(),
                parsedLink,
                tags == null ? List.of() : tags,
                filters == null ? List.of() : filters,
                null);

        links.put(parsedLink.uri().toString(), trackedLink);
        return trackedLink;
    }

    @Override
    public TrackedParsedLink removeLink(long chatId, URI url) {
        Map<String, TrackedParsedLink> links = storage.get(chatId);
        if (links == null) {
            return null;
        }
        return links.remove(url.toString());
    }

    @Override
    public Map<Long, List<TrackedParsedLink>> findAll() {
        return storage.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue().values())));
    }

    @Override
    public void updateLastUpdatedAt(long chatId, URI url, Instant lastUpdatedAt) {
        Map<String, TrackedParsedLink> links = storage.get(chatId);
        if (links == null) {
            return;
        }

        String key = url.toString();
        TrackedParsedLink current = links.get(key);
        if (current == null) {
            return;
        }

        TrackedParsedLink updated = new TrackedParsedLink(
                current.id(), current.parsedLink(), current.tags(), current.filters(), lastUpdatedAt);

        links.put(key, updated);
    }
}
