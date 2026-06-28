package com.zove.app.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zove.app.dto.SocialDtos.CreateStoryRequest;
import com.zove.app.dto.SocialDtos.StoryResponse;
import com.zove.app.model.MediaKind;
import com.zove.app.model.Story;
import com.zove.app.repository.FollowRepository;
import com.zove.app.repository.StoryRepository;

@Service
public class StoryService {

    private final UserService userService;
    private final FollowRepository followRepository;
    private final StoryRepository storyRepository;
    private final DtoMapper mapper;

    public StoryService(
            UserService userService,
            FollowRepository followRepository,
            StoryRepository storyRepository,
            DtoMapper mapper
    ) {
        this.userService = userService;
        this.followRepository = followRepository;
        this.storyRepository = storyRepository;
        this.mapper = mapper;
    }

    @Transactional
    public StoryResponse create(Long authorId, CreateStoryRequest request) {
        var author = userService.getRequiredUser(authorId);
        var story = storyRepository.save(new Story(
                author,
                request.mediaUrl().trim(),
                request.kind() == null ? MediaKind.IMAGE : request.kind(),
                request.caption() == null ? "" : request.caption().trim()
        ));
        return mapper.toStory(story);
    }

    @Transactional(readOnly = true)
    public List<StoryResponse> feed(Long userId) {
        var authorIds = new ArrayList<>(followRepository.findFollowingIds(userId));
        authorIds.add(userId);
        return storyRepository.findByAuthorIdInAndExpiresAtAfterOrderByCreatedAtDesc(authorIds, Instant.now()).stream()
                .map(mapper::toStory)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StoryResponse> byUser(Long userId) {
        userService.getRequiredUser(userId);
        return storyRepository.findByAuthorIdAndExpiresAtAfterOrderByCreatedAtDesc(userId, Instant.now()).stream()
                .map(mapper::toStory)
                .toList();
    }
}
