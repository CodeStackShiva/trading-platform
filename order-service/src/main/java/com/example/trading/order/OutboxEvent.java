package com.example.trading.order;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "outbox")
public class OutboxEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String topic;
    private String messageKey;
    private String payload;
    private boolean published;
    private Instant createdAt = Instant.now();

    protected OutboxEvent() {}
    public OutboxEvent(String topic, String messageKey, String payload) {
        this.topic = topic; this.messageKey = messageKey; this.payload = payload;
    }
    public String getTopic() { return topic; }
    public String getMessageKey() { return messageKey; }
    public String getPayload() { return payload; }
    public void markPublished() { this.published = true; }
}
