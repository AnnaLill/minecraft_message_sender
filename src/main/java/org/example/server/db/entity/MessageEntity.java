package org.example.server.db.entity;


import jakarta.persistence.*;
import java.util.UUID;

/**
 * JPA Entity для таблицы messages.
 *
 * <p>Хранит UUID игрока и текст сообщения (до 256 символов).
 * Поле id автоинкрементное (IDENTITY).</p>
 */
@Entity
@Table(name = "messages")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID uuid;

    @Column(nullable = false, length = 256)
    private String text;

    public MessageEntity() {}

    public MessageEntity(UUID uuid, String text) {
        this.uuid = uuid;
        this.text = text;
    }

    public Long getId() { return id; }
    public UUID getUuid() { return uuid; }
    public String getText() { return text; }
    public void setId(Long id) { this.id = id; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public void setText(String text) { this.text = text; }
}
