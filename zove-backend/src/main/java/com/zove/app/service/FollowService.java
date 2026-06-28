package com.zove.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.zove.app.dto.SocialDtos.FollowResponse;
import com.zove.app.model.Follow;
import com.zove.app.model.NotificationType;
import com.zove.app.repository.FollowRepository;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class FollowService {

    private final UserService userService;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    public FollowService(UserService userService, FollowRepository followRepository, NotificationService notificationService) {
        this.userService = userService;
        this.followRepository = followRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public FollowResponse follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(BAD_REQUEST, "You cannot follow yourself");
        }

        var follower = userService.getRequiredUser(followerId);
        var following = userService.getRequiredUser(followingId);

        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            followRepository.save(new Follow(follower, following));
            notificationService.create(
                    following,
                    follower,
                    NotificationType.FOLLOW,
                    follower.getDisplayName() + " followed you",
                    "USER",
                    follower.getId()
            );
        }

        return response(followingId, true);
    }

    @Transactional
    public FollowResponse unfollow(Long followerId, Long followingId) {
        userService.getRequiredUser(followingId);
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        return response(followingId, false);
    }

    private FollowResponse response(Long userId, boolean followed) {
        return new FollowResponse(
                userId,
                followed,
                followRepository.countByFollowingId(userId),
                followRepository.countByFollowerId(userId)
        );
    }
}
