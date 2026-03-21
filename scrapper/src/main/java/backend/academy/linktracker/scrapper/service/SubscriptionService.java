package backend.academy.linktracker.scrapper.service;

import backend.academy.linktracker.scrapper.model.Link;
import java.util.List;

public interface SubscriptionService {

    Link subscribe(long chatId, String url, List<String> tags);

    void unsubscribe(long chatId, String url);

    List<Link> getLinks(long chatId);

    List<Link> getLinks(long chatId, String tag);
}
