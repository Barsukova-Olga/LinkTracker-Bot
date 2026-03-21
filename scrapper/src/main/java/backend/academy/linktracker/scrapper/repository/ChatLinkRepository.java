package backend.academy.linktracker.scrapper.repository;

import java.util.List;

public interface ChatLinkRepository {

    void add(long chatId, long linkId);

    void remove(long chatId, long linkId);

    List<Long> findLinksByChatId(long chatId);

    List<Long> findChatsByLinkId(long linkId);
}
