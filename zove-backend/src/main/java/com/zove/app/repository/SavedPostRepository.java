package com.zove.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zove.app.model.SavedPost;

public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    Optional<SavedPost> findByPostIdAndUserId(Long postId, Long userId);

    Page<SavedPost> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
