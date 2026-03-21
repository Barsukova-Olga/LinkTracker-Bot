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
import backend.academy.linktracker.scrapper.model.GithubParsedLink;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.model.ParsedLink;
import backend.academy.linktracker.scrapper.model.StackoverflowParsedLink;
import backend.academy.linktracker.scrapper.repository.ChatRepository;
import java.net.URI;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ScrapperService {
    private final SubscriptionService subscriptionService;
    private final ChatRepository chatRepository;
    private final LinkParser linkParser;

    public ScrapperService(
            SubscriptionService subscriptionService, ChatRepository chatRepository, LinkParser linkParser) {
        this.subscriptionService = subscriptionService;
        this.chatRepository = chatRepository;
        this.linkParser = linkParser;
    }

    public void registerChat(long chatId) {
        if (chatRepository.exists(chatId)) {
            throw new ChatAlreadyExistsException(chatId);
        }

        chatRepository.add(chatId);
    }

    public void deleteChat(long chatId) {
        ensureChatExists(chatId);
        chatRepository.remove(chatId);
    }

    public ListLinksResponse getLinks(long chatId) {
        ensureChatExists(chatId);

        List<LinkResponse> links = subscriptionService.getLinks(chatId).stream()
                .map(this::toLinkResponse)
                .toList();

        return new ListLinksResponse(links, links.size());
    }

    public ListLinksResponse getLinks(long chatId, String tag) {
        ensureChatExists(chatId);

        List<LinkResponse> links = subscriptionService.getLinks(chatId, tag).stream()
                .map(this::toLinkResponse)
                .toList();

        return new ListLinksResponse(links, links.size());
    }

    public LinkResponse addLink(long chatId, AddLinkRequest request) {
        ensureChatExists(chatId);

        linkParser.parse(request.link().toString()).orElseThrow(() -> new InvalidLinkException(request.link()));

        boolean alreadyTracked = subscriptionService.getLinks(chatId).stream()
                .anyMatch(link -> link.url().equals(request.link().toString()));

        if (alreadyTracked) {
            throw new LinkAlreadyTrackedException(request.link());
        }

        Link link = subscriptionService.subscribe(chatId, request.link().toString(), request.tags());

        return toLinkResponse(link, request.tags(), request.filters());
    }

    public LinkResponse removeLink(long chatId, RemoveLinkRequest request) {
        ensureChatExists(chatId);

        ParsedLink requestParsed =
                linkParser.parse(request.link().toString()).orElseThrow(() -> new InvalidLinkException(request.link()));

        Link link = subscriptionService.getLinks(chatId).stream()
                .filter(item -> linkParser
                        .parse(item.url())
                        .map(parsed -> sameLink(parsed, requestParsed))
                        .orElse(false))
                .findFirst()
                .orElseThrow(() -> new LinkNotFoundException(request.link()));

        subscriptionService.unsubscribe(chatId, request.link().toString());

        return new LinkResponse(link.id(), request.link(), List.of(), List.of());
    }

    private void ensureChatExists(long chatId) {
        if (!chatRepository.exists(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
    }

    private LinkResponse toLinkResponse(Link link) {
        return new LinkResponse(link.id(), URI.create(link.url()), List.of(), List.of());
    }

    private LinkResponse toLinkResponse(Link link, List<String> tags, List<String> filters) {
        return new LinkResponse(link.id(), URI.create(link.url()), tags, filters);
    }

    private boolean sameLink(ParsedLink a, ParsedLink b) {
        if (a instanceof StackoverflowParsedLink sa && b instanceof StackoverflowParsedLink sb) {
            return sa.questionId() == sb.questionId();
        }

        if (a instanceof GithubParsedLink ga && b instanceof GithubParsedLink gb) {
            return ga.owner().equals(gb.owner()) && ga.repo().equals(gb.repo());
        }

        return false;
    }
}
