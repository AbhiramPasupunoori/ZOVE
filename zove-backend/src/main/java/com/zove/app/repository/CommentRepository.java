package com.zove.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zove.app.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    long countByPostId(Long postId);
}
