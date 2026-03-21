package backend.academy.linktracker.scrapper.orm.entity;

import java.io.Serializable;
import java.util.Objects;

public class LinkTagId implements Serializable {

    private Long chatId;
    private Long linkId;
    private String tag;

    public LinkTagId() {}

    public LinkTagId(Long chatId, Long linkId, String tag) {
        this.chatId = chatId;
        this.linkId = linkId;
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinkTagId that)) return false;
        return Objects.equals(chatId, that.chatId)
                && Objects.equals(linkId, that.linkId)
                && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, linkId, tag);
    }
}
