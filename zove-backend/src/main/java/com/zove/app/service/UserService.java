package com.zove.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.zove.app.dto.SocialDtos.ProfileResponse;
import com.zove.app.dto.SocialDtos.UpdateProfileRequest;
import com.zove.app.dto.SocialDtos.UserSummary;
import com.zove.app.model.AppUser;
import com.zove.app.repository.FollowRepository;
import com.zove.app.repository.UserRepository;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final DtoMapper mapper;

    public UserService(UserRepository userRepository, FollowRepository followRepository, DtoMapper mapper) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public AppUser getRequiredUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileById(Long userId, Long currentUserId) {
        return mapper.toProfile(getRequiredUser(userId), currentUserId);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUsername(String username, Long currentUserId) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        return mapper.toProfile(user, currentUserId);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        var user = getRequiredUser(userId);
        user.updateProfile(
                request.displayName().trim(),
                request.bio() == null ? "" : request.bio().trim(),
                blankToNull(request.avatarUrl())
        );
        return mapper.toProfile(user, userId);
    }

    @Transactional(readOnly = true)
    public List<UserSummary> getFollowers(Long userId) {
        getRequiredUser(userId);
        return followRepository.findByFollowingIdOrderByCreatedAtDesc(userId).stream()
                .map(follow -> mapper.toUserSummary(follow.getFollower()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserSummary> getFollowing(Long userId) {
        getRequiredUser(userId);
        return followRepository.findByFollowerIdOrderByCreatedAtDesc(userId).stream()
                .map(follow -> mapper.toUserSummary(follow.getFollowing()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserSummary> searchUsers(String query) {
        return userRepository
                .findTop20ByUsernameContainingIgnoreCaseOrDisplayNameContainingIgnoreCaseOrderByUsernameAsc(query, query)
                .stream()
                .map(mapper::toUserSummary)
                .toList();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
