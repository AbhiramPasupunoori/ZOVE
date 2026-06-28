package com.zove.app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zove.app.dto.SocialDtos.PageResponse;
import com.zove.app.dto.SocialDtos.PostResponse;
import com.zove.app.service.PostService;

@RestController
@RequestMapping("/api/hashtags")
public class HashtagController {

    private final PostService postService;

    public HashtagController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{tag}/posts")
    public PageResponse<PostResponse> posts(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return postService.hashtag(Long.valueOf(jwt.getSubject()), tag, page, size);
    }
}
