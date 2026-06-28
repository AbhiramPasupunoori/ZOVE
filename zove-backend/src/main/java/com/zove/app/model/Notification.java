package com.zove.app.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private AppUser recipient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private AppUser actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Column(nullable = false, length = 220)
    private String message;

    @Column(length = 40)
    private String targetType;

    private Long targetId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant readAt;

    protected Notification() {
    }

    public Notification(AppUser recipient, AppUser actor, NotificationType type, String message, String targetType, Long targetId) {
        this.recipient = recipient;
        this.actor = actor;
        this.type = type;
        this.message = message;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public void markRead() {
        if (readAt == null) {
            readAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public AppUser getRecipient() {
        return recipient;
    }

    public AppUser getActor() {
        return actor;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getTargetType() {
        return targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }
}
