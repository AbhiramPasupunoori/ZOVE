package com.zove.app.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zove.app.dto.SocialDtos.CreateStoryRequest;
import com.zove.app.dto.SocialDtos.StoryResponse;
import com.zove.app.service.StoryService;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    public StoryResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateStoryRequest request) {
        return storyService.create(Long.valueOf(jwt.getSubject()), request);
    }

    @GetMapping("/feed")
    public List<StoryResponse> feed(@AuthenticationPrincipal Jwt jwt) {
        return storyService.feed(Long.valueOf(jwt.getSubject()));
    }

    @GetMapping("/users/{userId}")
    public List<StoryResponse> byUser(@PathVariable Long userId) {
        return storyService.byUser(userId);
    }
}
