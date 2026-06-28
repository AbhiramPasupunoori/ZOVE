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
@Table(name = "stories")
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private AppUser author;

    @Column(nullable = false, length = 500)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MediaKind mediaKind;

    @Column(length = 300)
    private String caption;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    protected Story() {
    }

    public Story(AppUser author, String mediaUrl, MediaKind mediaKind, String caption) {
        this.author = author;
        this.mediaUrl = mediaUrl;
        this.mediaKind = mediaKind == null ? MediaKind.IMAGE : mediaKind;
        this.caption = caption;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        expiresAt = createdAt.plusSeconds(24 * 60 * 60);
    }

    public Long getId() {
        return id;
    }

    public AppUser getAuthor() {
        return author;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public MediaKind getMediaKind() {
        return mediaKind;
    }

    public String getCaption() {
        return caption;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
