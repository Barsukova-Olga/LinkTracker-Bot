package backend.academy.linktracker.scrapper.orm.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chats")
public class ChatEntity {

    @Id
    private Long id;

    public ChatEntity() {}

    public ChatEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
