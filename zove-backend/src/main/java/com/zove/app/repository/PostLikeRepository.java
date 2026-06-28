package com.zove.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zove.app.model.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);
}
