package backend.academy.linktracker.scrapper.controller;

import backend.academy.linktracker.scrapper.dto.AddLinkRequest;
import backend.academy.linktracker.scrapper.dto.LinkResponse;
import backend.academy.linktracker.scrapper.dto.ListLinksResponse;
import backend.academy.linktracker.scrapper.dto.RemoveLinkRequest;
import backend.academy.linktracker.scrapper.service.ScrapperService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ScrapperController {
    private final ScrapperService scrapperService;

    public ScrapperController(ScrapperService scrapperService) {
        this.scrapperService = scrapperService;
    }

    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<Void> registerChat(@PathVariable long id) {
        scrapperService.registerChat(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable long id) {
        scrapperService.deleteChat(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> getLinks(
            @RequestHeader("Tg-Chat-Id") long chatId, @RequestParam(required = false) String tag) {
        return ResponseEntity.ok(scrapperService.getLinks(chatId, tag));
    }

    @PostMapping("/links")
    public ResponseEntity<LinkResponse> addLink(
            @RequestHeader("Tg-Chat-Id") long chatId, @Valid @RequestBody AddLinkRequest request) {
        return ResponseEntity.ok(scrapperService.addLink(chatId, request));
    }

    @DeleteMapping("/links")
    public ResponseEntity<LinkResponse> removeLink(
            @RequestHeader("Tg-Chat-Id") long chatId, @Valid @RequestBody RemoveLinkRequest request) {
        return ResponseEntity.ok(scrapperService.removeLink(chatId, request));
    }
}
