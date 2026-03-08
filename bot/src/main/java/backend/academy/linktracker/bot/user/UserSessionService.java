package backend.academy.linktracker.bot.user;

import java.net.URI;
import java.util.Optional;

public interface UserSessionService {
    Optional<UserSession> getSession(long charId);

    boolean hasActiveSession(long chatId);

    void startSession(long charId);

    void setLinkSession(long charId, URI userLink);

    void clearSession(long charId);
}
