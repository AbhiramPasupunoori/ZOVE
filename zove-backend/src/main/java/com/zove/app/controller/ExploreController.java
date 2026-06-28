package com.zove.app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zove.app.dto.SocialDtos.PageResponse;
import com.zove.app.dto.SocialDtos.PostResponse;
import com.zove.app.service.PostService;

@RestController
@RequestMapping("/api/explore")
public class ExploreController {

    private final PostService postService;

    public ExploreController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public PageResponse<PostResponse> posts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return postService.explore(Long.valueOf(jwt.getSubject()), page, size);
    }
}
