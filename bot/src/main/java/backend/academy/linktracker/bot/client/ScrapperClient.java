package backend.academy.linktracker.bot.client;

import backend.academy.linktracker.bot.dto.AddLinkRequest;
import backend.academy.linktracker.bot.dto.LinkResponse;
import backend.academy.linktracker.bot.dto.ListLinksResponse;
import backend.academy.linktracker.bot.dto.RemoveLinkRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ScrapperClient {

    private final RestClient restClient;

    public ScrapperClient(RestClient scrapperRestClient) {
        this.restClient = scrapperRestClient;
    }

    public ListLinksResponse getLinks(long chatId, String tag) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/links");

        if (tag != null && !tag.isBlank()) {
            builder.queryParam("tag", tag);
        }

        return restClient
                .get()
                .uri(builder.build().toUriString())
                .header("Tg-Chat-Id", String.valueOf(chatId))
                .retrieve()
                .body(ListLinksResponse.class);
    }

    public void registerChat(long chatId) {
        restClient.post().uri("/tg-chat/{id}", chatId).retrieve().toBodilessEntity();
    }

    public LinkResponse addLink(long chatId, AddLinkRequest request) {
        return restClient
                .post()
                .uri("/links")
                .header("Tg-Chat-Id", String.valueOf(chatId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(LinkResponse.class);
    }

    public LinkResponse removeLink(long chatId, RemoveLinkRequest request) {
        return restClient
                .method(HttpMethod.DELETE)
                .uri("/links")
                .header("Tg-Chat-Id", String.valueOf(chatId))
                .body(request)
                .retrieve()
                .body(LinkResponse.class);
    }
}
