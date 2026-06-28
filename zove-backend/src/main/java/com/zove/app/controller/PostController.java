package com.zove.app.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zove.app.dto.SocialDtos.CommentRequest;
import com.zove.app.dto.SocialDtos.CommentResponse;
import com.zove.app.dto.SocialDtos.CreatePostRequest;
import com.zove.app.dto.SocialDtos.PageResponse;
import com.zove.app.dto.SocialDtos.PostResponse;
import com.zove.app.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public PageResponse<PostResponse> list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return postService.list(userId(jwt), page, size);
    }

    @GetMapping("/reels")
    public PageResponse<PostResponse> reels(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return postService.reels(userId(jwt), page, size);
    }

    @GetMapping("/saved")
    public PageResponse<PostResponse> saved(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return postService.saved(userId(jwt), page, size);
    }

    @PostMapping
    public PostResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreatePostRequest request) {
        return postService.create(userId(jwt), request);
    }

    @GetMapping("/{postId}")
    public PostResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) {
        return postService.get(userId(jwt), postId);
    }

    @GetMapping("/users/{userId}")
    public PageResponse<PostResponse> byUser(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return postService.postsByUser(userId(jwt), userId, page, size);
    }

    @PostMapping("/{postId}/likes")
    public PostResponse like(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) {
        return postService.like(userId(jwt), postId);
    }

    @DeleteMapping("/{postId}/likes")
    public PostResponse unlike(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) {
        return postService.unlike(userId(jwt), postId);
    }

    @PostMapping("/{postId}/saves")
    public PostResponse save(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) {
        return postService.save(userId(jwt), postId);
    }

    @DeleteMapping("/{postId}/saves")
    public PostResponse unsave(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) {
        return postService.unsave(userId(jwt), postId);
    }

    @GetMapping("/{postId}/comments")
    public List<CommentResponse> comments(@PathVariable Long postId) {
        return postService.comments(postId);
    }

    @PostMapping("/{postId}/comments")
    public CommentResponse comment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request
    ) {
        return postService.comment(userId(jwt), postId, request);
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
