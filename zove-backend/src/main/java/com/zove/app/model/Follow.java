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
        name = "follows",
        uniqueConstraints = @UniqueConstraint(name = "uk_follows_follower_following", columnNames = {"follower_id", "following_id"})
)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private AppUser follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", nullable = false)
    private AppUser following;

    private Instant createdAt;

    protected Follow() {
    }

    public Follow(AppUser follower, AppUser following) {
        this.follower = follower;
        this.following = following;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public AppUser getFollower() {
        return follower;
    }

    public AppUser getFollowing() {
        return following;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
