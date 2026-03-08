package backend.academy.linktracker.scrapper.service;

import backend.academy.linktracker.scrapper.dto.AddLinkRequest;
import backend.academy.linktracker.scrapper.dto.LinkResponse;
import backend.academy.linktracker.scrapper.dto.ListLinksResponse;
import backend.academy.linktracker.scrapper.dto.RemoveLinkRequest;
import backend.academy.linktracker.scrapper.exception.ChatAlreadyExistsException;
import backend.academy.linktracker.scrapper.exception.ChatNotFoundException;
import backend.academy.linktracker.scrapper.exception.InvalidLinkException;
import backend.academy.linktracker.scrapper.exception.LinkAlreadyTrackedException;
import backend.academy.linktracker.scrapper.exception.LinkNotFoundException;
import backend.academy.linktracker.scrapper.link.parser.LinkParser;
import backend.academy.linktracker.scrapper.model.ParsedLink;
import backend.academy.linktracker.scrapper.model.TrackedParsedLink;
import backend.academy.linktracker.scrapper.repository.ScrapperRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ScrapperService {
    private final ScrapperRepository scrapperRepository;
    private final LinkParser linkParser;

    public ScrapperService(ScrapperRepository scrapperRepository, LinkParser linkParser) {
        this.scrapperRepository = scrapperRepository;
        this.linkParser = linkParser;
    }

    public void registerChat(long chatId) {
        if (scrapperRepository.chatExists(chatId)) {
            throw new ChatAlreadyExistsException(chatId);
        }

        scrapperRepository.registerChat(chatId);
    }

    public void deleteChat(long chatId) {
        ensureChatExists(chatId);
        scrapperRepository.deleteChat(chatId);
    }

    public ListLinksResponse getLinks(long chatId) {
        ensureChatExists(chatId);

        List<LinkResponse> links = scrapperRepository.getLinks(chatId).stream()
                .map(this::toLinkResponse)
                .toList();

        return new ListLinksResponse(links, links.size());
    }

    public LinkResponse addLink(long chatId, AddLinkRequest request) {
        ensureChatExists(chatId);

        ParsedLink parsedLink =
                linkParser.parse(request.link().toString()).orElseThrow(() -> new InvalidLinkException(request.link()));

        boolean alreadyTracked = scrapperRepository.getLinks(chatId).stream()
                .anyMatch(link -> link.parsedLink().uri().equals(request.link()));

        if (alreadyTracked) {
            throw new LinkAlreadyTrackedException(request.link());
        }

        TrackedParsedLink trackedLink =
                scrapperRepository.addLink(chatId, parsedLink, request.tags(), request.filters());

        return toLinkResponse(trackedLink);
    }

    public LinkResponse removeLink(long chatId, RemoveLinkRequest request) {
        ensureChatExists(chatId);

        TrackedParsedLink removedLink = scrapperRepository.removeLink(chatId, request.link());

        if (removedLink == null) {
            throw new LinkNotFoundException(request.link());
        }

        return toLinkResponse(removedLink);
    }

    private void ensureChatExists(long chatId) {
        if (!scrapperRepository.chatExists(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
    }

    private LinkResponse toLinkResponse(TrackedParsedLink trackedLink) {
        return new LinkResponse(
                trackedLink.id(), trackedLink.parsedLink().uri(), trackedLink.tags(), trackedLink.filters());
    }
}
