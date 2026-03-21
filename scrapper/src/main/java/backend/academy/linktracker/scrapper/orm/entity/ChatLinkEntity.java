package backend.academy.linktracker.scrapper.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_link")
@IdClass(ChatLinkId.class)
public class ChatLinkEntity {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Id
    @Column(name = "link_id")
    private Long linkId;

    public ChatLinkEntity() {}

    public ChatLinkEntity(Long chatId, Long linkId) {
        this.chatId = chatId;
        this.linkId = linkId;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getLinkId() {
        return linkId;
    }
}
