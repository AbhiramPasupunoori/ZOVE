package com.zove.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zove.app.model.PostHashtag;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    List<PostHashtag> findByPostIdOrderByIdAsc(Long postId);

    List<PostHashtag> findByTagOrderByPostCreatedAtDesc(String tag);

    List<PostHashtag> findTop20ByTagContainingIgnoreCaseOrderByTagAsc(String tag);

    long countByTag(String tag);
}
