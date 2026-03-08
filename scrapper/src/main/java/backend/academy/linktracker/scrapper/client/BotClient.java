package backend.academy.linktracker.scrapper.client;

import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BotClient {

    private final RestClient botRestClient;

    public BotClient(RestClient botRestClient) {
        this.botRestClient = botRestClient;
    }

    public void sendUpdate(LinkUpdateRequest request) {
        botRestClient.post().uri("/updates").body(request).retrieve().toBodilessEntity();
    }
}
