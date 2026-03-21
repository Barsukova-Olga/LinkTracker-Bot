package backend.academy.linktracker.scrapper.repository;

public interface ChatRepository {
    void add(long chatId);

    void remove(long chatId);

    boolean exists(long chatId);
}
