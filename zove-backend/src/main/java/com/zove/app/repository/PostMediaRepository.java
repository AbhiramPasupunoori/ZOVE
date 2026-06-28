package com.zove.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zove.app.model.PostMedia;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {

    List<PostMedia> findByPostIdOrderBySortOrderAsc(Long postId);
}
