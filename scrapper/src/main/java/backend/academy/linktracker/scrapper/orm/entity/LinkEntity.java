package backend.academy.linktracker.scrapper.orm.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Setter;

@Entity
@Setter
@Table(name = "links")
public class LinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    @Column(name = "created_at")
    private Instant createdAt;

    public LinkEntity() {}

    public LinkEntity(String url) {
        this.url = url;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setLastUpdatedAt(Instant lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
