package com.zove.app.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zove.app.model.Story;

public interface StoryRepository extends JpaRepository<Story, Long> {

    List<Story> findByAuthorIdInAndExpiresAtAfterOrderByCreatedAtDesc(Collection<Long> authorIds, Instant now);

    List<Story> findByAuthorIdAndExpiresAtAfterOrderByCreatedAtDesc(Long authorId, Instant now);
}
