package com.zove.app.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zove.app.dto.SocialDtos.FollowResponse;
import com.zove.app.dto.SocialDtos.ProfileResponse;
import com.zove.app.dto.SocialDtos.UpdateProfileRequest;
import com.zove.app.dto.SocialDtos.UserSummary;
import com.zove.app.service.FollowService;
import com.zove.app.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    public UserController(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
    }

    @GetMapping("/me")
    public ProfileResponse me(@AuthenticationPrincipal Jwt jwt) {
        return userService.getProfileById(userId(jwt), userId(jwt));
    }

    @PatchMapping("/me")
    public ProfileResponse updateMe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateProfile(userId(jwt), request);
    }

    @GetMapping("/{username}")
    public ProfileResponse byUsername(@AuthenticationPrincipal Jwt jwt, @PathVariable String username) {
        return userService.getProfileByUsername(username, userId(jwt));
    }

    @PostMapping("/{userId}/follow")
    public FollowResponse follow(@AuthenticationPrincipal Jwt jwt, @PathVariable Long userId) {
        return followService.follow(userId(jwt), userId);
    }

    @DeleteMapping("/{userId}/follow")
    public FollowResponse unfollow(@AuthenticationPrincipal Jwt jwt, @PathVariable Long userId) {
        return followService.unfollow(userId(jwt), userId);
    }

    @GetMapping("/{userId}/followers")
    public List<UserSummary> followers(@PathVariable Long userId) {
        return userService.getFollowers(userId);
    }

    @GetMapping("/{userId}/following")
    public List<UserSummary> following(@PathVariable Long userId) {
        return userService.getFollowing(userId);
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
