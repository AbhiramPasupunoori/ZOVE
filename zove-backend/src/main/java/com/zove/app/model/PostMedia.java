package com.zove.app.model;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "post_media",
        uniqueConstraints = @UniqueConstraint(name = "uk_post_media_position", columnNames = {"post_id", "sort_order"})
)
public class PostMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, length = 500)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MediaKind mediaKind;

    @Column(nullable = false)
    private int sortOrder;

    @Column(length = 160)
    private String altText;

    protected PostMedia() {
    }

    public PostMedia(Post post, String mediaUrl, MediaKind mediaKind, int sortOrder, String altText) {
        this.post = post;
        this.mediaUrl = mediaUrl;
        this.mediaKind = mediaKind == null ? MediaKind.IMAGE : mediaKind;
        this.sortOrder = sortOrder;
        this.altText = altText;
    }

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public MediaKind getMediaKind() {
        return mediaKind;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public String getAltText() {
        return altText;
    }
}
