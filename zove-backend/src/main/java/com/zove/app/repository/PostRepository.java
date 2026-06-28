package com.zove.app.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zove.app.model.Post;
import com.zove.app.model.PostKind;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    Page<Post> findByAuthorIdInOrderByCreatedAtDesc(Collection<Long> authorIds, Pageable pageable);

    Page<Post> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String content, Pageable pageable);

    Page<Post> findByKindOrderByCreatedAtDesc(PostKind kind, Pageable pageable);

    long countByAuthorId(Long authorId);
}
