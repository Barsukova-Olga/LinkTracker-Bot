package backend.academy.linktracker.scrapper.repository;

import backend.academy.linktracker.scrapper.model.Link;
import java.util.List;

public interface LinkTagRepository {

    void addTag(long chatId, long linkId, String tag);

    void removeTag(long chatId, long linkId, String tag);

    List<String> findTags(long chatId, long linkId);

    void removeAllTags(long chatId, long linkId);

    List<Link> findLinksByTag(long chatId, String tag);
}
