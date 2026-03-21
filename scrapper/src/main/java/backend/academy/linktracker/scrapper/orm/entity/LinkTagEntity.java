package backend.academy.linktracker.scrapper.orm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "link_tag")
@IdClass(LinkTagId.class)
public class LinkTagEntity {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Id
    @Column(name = "link_id")
    private Long linkId;

    @Id
    @Column(name = "tag")
    private String tag;

    public LinkTagEntity() {}

    public LinkTagEntity(Long chatId, Long linkId, String tag) {
        this.chatId = chatId;
        this.linkId = linkId;
        this.tag = tag;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getLinkId() {
        return linkId;
    }

    public String getTag() {
        return tag;
    }
}
