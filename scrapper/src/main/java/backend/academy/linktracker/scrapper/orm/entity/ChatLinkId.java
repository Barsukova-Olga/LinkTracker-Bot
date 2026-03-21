package backend.academy.linktracker.scrapper.orm.entity;

import java.io.Serializable;
import java.util.Objects;

public class ChatLinkId implements Serializable {

    private Long chatId;
    private Long linkId;

    public ChatLinkId() {}

    public ChatLinkId(Long chatId, Long linkId) {
        this.chatId = chatId;
        this.linkId = linkId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatLinkId that)) return false;
        return Objects.equals(chatId, that.chatId) && Objects.equals(linkId, that.linkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, linkId);
    }
}
