package backend.academy.linktracker.bot.user;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class BotUserSessionService implements UserSessionService {
    private final Map<Long, UserSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public Optional<UserSession> getSession(long charId) {
        return Optional.ofNullable(sessionMap.get(charId));
    }

    @Override
    public boolean hasActiveSession(long chatId) {
        return sessionMap.containsKey(chatId);
    }

    @Override
    public void startSession(long charId) {
        sessionMap.put(charId, new UserSession(charId, UserState.WAITING_LINK, null));
    }

    @Override
    public void setLinkSession(long charId, URI userLink) {
        UserSession userSession = getSession(charId)
                .orElseThrow(() -> new IllegalStateException("No active session for chatId=" + charId));
        userSession.setUserLink(userLink);
        userSession.setState(UserState.WAITING_TAG);
    }

    @Override
    public void clearSession(long charId) {
        sessionMap.remove(charId);
    }
}
