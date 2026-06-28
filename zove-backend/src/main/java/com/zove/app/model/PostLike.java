package com.zove.app.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "post_likes",
        uniqueConstraints = @UniqueConstraint(name = "uk_post_likes_post_user", columnNames = {"post_id", "user_id"})
)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    private Instant createdAt;

    protected PostLike() {
    }

    public PostLike(Post post, AppUser user) {
        this.post = post;
        this.user = user;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public AppUser getUser() {
        return user;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
