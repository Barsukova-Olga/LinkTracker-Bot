package backend.academy.linktracker.bot.user;

import java.net.URI;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserSession {
    private final long chatId;

    @Setter
    private UserState state;

    @Setter
    private URI userLink;

    public UserSession(long chatId, UserState state, URI userLink) {
        this.chatId = chatId;
        this.state = state;
        this.userLink = userLink;
    }
}
