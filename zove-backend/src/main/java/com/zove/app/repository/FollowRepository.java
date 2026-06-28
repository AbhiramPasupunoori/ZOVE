package com.zove.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zove.app.model.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    long countByFollowerId(Long followerId);

    long countByFollowingId(Long followingId);

    List<Follow> findByFollowerIdOrderByCreatedAtDesc(Long followerId);

    List<Follow> findByFollowingIdOrderByCreatedAtDesc(Long followingId);

    @Query("select f.following.id from Follow f where f.follower.id = :followerId")
    List<Long> findFollowingIds(@Param("followerId") Long followerId);
}
