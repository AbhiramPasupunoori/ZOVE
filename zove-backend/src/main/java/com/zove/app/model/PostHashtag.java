package com.zove.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "post_hashtags",
        uniqueConstraints = @UniqueConstraint(name = "uk_post_hashtags_post_tag", columnNames = {"post_id", "tag"})
)
public class PostHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, length = 60)
    private String tag;

    protected PostHashtag() {
    }

    public PostHashtag(Post post, String tag) {
        this.post = post;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public String getTag() {
        return tag;
    }
}
