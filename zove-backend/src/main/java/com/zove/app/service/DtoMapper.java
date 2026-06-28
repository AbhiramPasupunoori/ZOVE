package com.zove.app.service;

import org.springframework.stereotype.Component;

import com.zove.app.dto.SocialDtos.CommentResponse;
import com.zove.app.dto.SocialDtos.ConversationResponse;
import com.zove.app.dto.SocialDtos.MessageResponse;
import com.zove.app.dto.SocialDtos.MediaResponse;
import com.zove.app.dto.SocialDtos.NotificationResponse;
import com.zove.app.dto.SocialDtos.PostResponse;
import com.zove.app.dto.SocialDtos.ProfileResponse;
import com.zove.app.dto.SocialDtos.StoryResponse;
import com.zove.app.dto.SocialDtos.UserSummary;
import com.zove.app.model.AppUser;
import com.zove.app.model.ChatMessage;
import com.zove.app.model.Comment;
import com.zove.app.model.Conversation;
import com.zove.app.model.Notification;
import com.zove.app.model.Post;
import com.zove.app.model.PostMedia;
import com.zove.app.model.Story;
import com.zove.app.repository.ChatMessageRepository;
import com.zove.app.repository.CommentRepository;
import com.zove.app.repository.FollowRepository;
import com.zove.app.repository.PostHashtagRepository;
import com.zove.app.repository.PostLikeRepository;
import com.zove.app.repository.PostMediaRepository;
import com.zove.app.repository.PostRepository;
import com.zove.app.repository.SavedPostRepository;

@Component
public class DtoMapper {

    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PostMediaRepository postMediaRepository;
    private final SavedPostRepository savedPostRepository;
    private final PostHashtagRepository postHashtagRepository;

    public DtoMapper(
            FollowRepository followRepository,
            PostRepository postRepository,
            PostLikeRepository postLikeRepository,
            CommentRepository commentRepository,
            ChatMessageRepository chatMessageRepository,
            PostMediaRepository postMediaRepository,
            SavedPostRepository savedPostRepository,
            PostHashtagRepository postHashtagRepository
    ) {
        this.followRepository = followRepository;
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.postMediaRepository = postMediaRepository;
        this.savedPostRepository = savedPostRepository;
        this.postHashtagRepository = postHashtagRepository;
    }

    public UserSummary toUserSummary(AppUser user) {
        return new UserSummary(
                user.getId(),
                user.getDisplayName(),
                user.getUsername(),
                user.getAvatarUrl()
        );
    }

    public ProfileResponse toProfile(AppUser user, Long currentUserId) {
        var followed = currentUserId != null
                && !currentUserId.equals(user.getId())
                && followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId());
        return new ProfileResponse(
                user.getId(),
                user.getDisplayName(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getAvatarUrl(),
                followRepository.countByFollowingId(user.getId()),
                followRepository.countByFollowerId(user.getId()),
                postRepository.countByAuthorId(user.getId()),
                followed,
                user.getCreatedAt()
        );
    }

    public PostResponse toPost(Post post, Long currentUserId) {
        var liked = currentUserId != null
                && postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId);
        var saved = currentUserId != null
                && savedPostRepository.existsByPostIdAndUserId(post.getId(), currentUserId);
        return new PostResponse(
                post.getId(),
                toUserSummary(post.getAuthor()),
                post.getContent(),
                post.getMediaUrl(),
                post.getKind(),
                postMediaRepository.findByPostIdOrderBySortOrderAsc(post.getId()).stream()
                        .map(this::toMedia)
                        .toList(),
                postHashtagRepository.findByPostIdOrderByIdAsc(post.getId()).stream()
                        .map(hashtag -> hashtag.getTag())
                        .toList(),
                postLikeRepository.countByPostId(post.getId()),
                commentRepository.countByPostId(post.getId()),
                liked,
                saved,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public MediaResponse toMedia(PostMedia media) {
        return new MediaResponse(
                media.getId(),
                media.getMediaUrl(),
                media.getMediaKind(),
                media.getSortOrder(),
                media.getAltText()
        );
    }

    public CommentResponse toComment(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                toUserSummary(comment.getAuthor()),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    public NotificationResponse toNotification(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                toUserSummary(notification.getActor()),
                notification.getType(),
                notification.getMessage(),
                notification.getTargetType(),
                notification.getTargetId(),
                notification.getReadAt() != null,
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }

    public MessageResponse toMessage(ChatMessage message) {
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                toUserSummary(message.getSender()),
                toUserSummary(message.getRecipient()),
                message.getContent(),
                message.getReadAt() != null,
                message.getCreatedAt(),
                message.getReadAt()
        );
    }

    public ConversationResponse toConversation(Conversation conversation, Long currentUserId) {
        var participant = conversation.getUserOne().getId().equals(currentUserId)
                ? conversation.getUserTwo()
                : conversation.getUserOne();
        var lastMessage = chatMessageRepository.findTopByConversationIdOrderByCreatedAtDesc(conversation.getId())
                .map(this::toMessage)
                .orElse(null);
        return new ConversationResponse(
                conversation.getId(),
                toUserSummary(participant),
                lastMessage,
                chatMessageRepository.countByConversationIdAndRecipientIdAndReadAtIsNull(conversation.getId(), currentUserId),
                conversation.getUpdatedAt()
        );
    }

    public StoryResponse toStory(Story story) {
        return new StoryResponse(
                story.getId(),
                toUserSummary(story.getAuthor()),
                story.getMediaUrl(),
                story.getMediaKind(),
                story.getCaption(),
                story.getCreatedAt(),
                story.getExpiresAt()
        );
    }
}
