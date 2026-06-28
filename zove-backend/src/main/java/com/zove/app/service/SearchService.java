package com.zove.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zove.app.dto.SocialDtos.HashtagResponse;
import com.zove.app.dto.SocialDtos.SearchResponse;
import com.zove.app.repository.PostHashtagRepository;

import java.util.LinkedHashSet;

@Service
public class SearchService {

    private final UserService userService;
    private final PostService postService;
    private final PostHashtagRepository postHashtagRepository;

    public SearchService(UserService userService, PostService postService, PostHashtagRepository postHashtagRepository) {
        this.userService = userService;
        this.postService = postService;
        this.postHashtagRepository = postHashtagRepository;
    }

    @Transactional(readOnly = true)
    public SearchResponse search(Long currentUserId, String query) {
        var normalizedQuery = query == null ? "" : query.trim();
        if (normalizedQuery.isBlank()) {
            return new SearchResponse(java.util.List.of(), java.util.List.of(), java.util.List.of());
        }
        var users = userService.searchUsers(normalizedQuery);
        var posts = postService.search(currentUserId, normalizedQuery, 0, 20).items();
        var seenTags = new LinkedHashSet<String>();
        var hashtags = postHashtagRepository
                .findTop20ByTagContainingIgnoreCaseOrderByTagAsc(normalizedQuery.replaceFirst("^#", ""))
                .stream()
                .map(hashtag -> hashtag.getTag())
                .filter(seenTags::add)
                .map(tag -> new HashtagResponse(tag, postHashtagRepository.countByTag(tag)))
                .toList();
        return new SearchResponse(users, posts, hashtags);
    }
}
