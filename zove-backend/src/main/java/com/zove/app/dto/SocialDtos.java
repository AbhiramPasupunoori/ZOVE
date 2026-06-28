package com.zove.app.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.zove.app.model.NotificationType;
import com.zove.app.model.MediaKind;
import com.zove.app.model.PostKind;

public final class SocialDtos {

    private SocialDtos() {
    }

    public record PageResponse<T>(
            List<T> items,
            int page,
            int size,
            long totalItems,
            int totalPages
    ) {
    }

    public record UserSummary(
            Long id,
            String displayName,
            String username,
            String avatarUrl
    ) {
    }

    public record ProfileResponse(
            Long id,
            String displayName,
            String username,
            String email,
            String bio,
            String avatarUrl,
            long followersCount,
            long followingCount,
            long postsCount,
            boolean followedByCurrentUser,
            Instant createdAt
    ) {
    }

    public record UpdateProfileRequest(
            @NotBlank @Size(min = 2, max = 80) String displayName,
            @Size(max = 240) String bio,
            @Size(max = 500) String avatarUrl
    ) {
    }

    public record CreatePostRequest(
            @NotBlank @Size(max = 1200) String content,
            @Size(max = 500) String mediaUrl,
            PostKind kind,
            @Size(max = 10) List<MediaItemRequest> media
    ) {
    }

    public record MediaItemRequest(
            @NotBlank @Size(max = 500) String url,
            MediaKind kind,
            @Size(max = 160) String altText
    ) {
    }

    public record MediaResponse(
            Long id,
            String url,
            MediaKind kind,
            int sortOrder,
            String altText
    ) {
    }

    public record PostResponse(
            Long id,
            UserSummary author,
            String content,
            String mediaUrl,
            PostKind kind,
            List<MediaResponse> media,
            List<String> hashtags,
            long likesCount,
            long commentsCount,
            boolean likedByCurrentUser,
            boolean savedByCurrentUser,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    public record CommentRequest(
            @NotBlank @Size(max = 600) String content
    ) {
    }

    public record CommentResponse(
            Long id,
            Long postId,
            UserSummary author,
            String content,
            Instant createdAt
    ) {
    }

    public record FollowResponse(
            Long userId,
            boolean followed,
            long followersCount,
            long followingCount
    ) {
    }

    public record NotificationResponse(
            Long id,
            UserSummary actor,
            NotificationType type,
            String message,
            String targetType,
            Long targetId,
            boolean read,
            Instant createdAt,
            Instant readAt
    ) {
    }

    public record CountResponse(
            long count
    ) {
    }

    public record UploadResponse(
            String url,
            String fileName,
            long size
    ) {
    }

    public record SearchResponse(
            List<UserSummary> users,
            List<PostResponse> posts,
            List<HashtagResponse> hashtags
    ) {
    }

    public record HashtagResponse(
            String tag,
            long postsCount
    ) {
    }

    public record CreateStoryRequest(
            @NotBlank @Size(max = 500) String mediaUrl,
            MediaKind kind,
            @Size(max = 300) String caption
    ) {
    }

    public record StoryResponse(
            Long id,
            UserSummary author,
            String mediaUrl,
            MediaKind kind,
            String caption,
            Instant createdAt,
            Instant expiresAt
    ) {
    }

    public record SendMessageRequest(
            @NotNull Long recipientId,
            @NotBlank @Size(max = 1600) String content
    ) {
    }

    public record MessageResponse(
            Long id,
            Long conversationId,
            UserSummary sender,
            UserSummary recipient,
            String content,
            boolean read,
            Instant createdAt,
            Instant readAt
    ) {
    }

    public record ConversationResponse(
            Long id,
            UserSummary participant,
            MessageResponse lastMessage,
            long unreadCount,
            Instant updatedAt
    ) {
    }
}
