package com.zove.app.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.zove.app.dto.SocialDtos.CommentRequest;
import com.zove.app.dto.SocialDtos.CommentResponse;
import com.zove.app.dto.SocialDtos.CreatePostRequest;
import com.zove.app.dto.SocialDtos.MediaItemRequest;
import com.zove.app.dto.SocialDtos.PageResponse;
import com.zove.app.dto.SocialDtos.PostResponse;
import com.zove.app.model.Comment;
import com.zove.app.model.MediaKind;
import com.zove.app.model.NotificationType;
import com.zove.app.model.Post;
import com.zove.app.model.PostHashtag;
import com.zove.app.model.PostKind;
import com.zove.app.model.PostLike;
import com.zove.app.model.PostMedia;
import com.zove.app.model.SavedPost;
import com.zove.app.repository.CommentRepository;
import com.zove.app.repository.FollowRepository;
import com.zove.app.repository.PostHashtagRepository;
import com.zove.app.repository.PostLikeRepository;
import com.zove.app.repository.PostMediaRepository;
import com.zove.app.repository.PostRepository;
import com.zove.app.repository.SavedPostRepository;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PostService {

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("(?<!\\w)#([a-zA-Z0-9_]{1,50})");

    private final UserService userService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostMediaRepository postMediaRepository;
    private final SavedPostRepository savedPostRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;
    private final DtoMapper mapper;

    public PostService(
            UserService userService,
            PostRepository postRepository,
            CommentRepository commentRepository,
            PostLikeRepository postLikeRepository,
            PostMediaRepository postMediaRepository,
            SavedPostRepository savedPostRepository,
            PostHashtagRepository postHashtagRepository,
            FollowRepository followRepository,
            NotificationService notificationService,
            DtoMapper mapper
    ) {
        this.userService = userService;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
        this.postMediaRepository = postMediaRepository;
        this.savedPostRepository = savedPostRepository;
        this.postHashtagRepository = postHashtagRepository;
        this.followRepository = followRepository;
        this.notificationService = notificationService;
        this.mapper = mapper;
    }

    @Transactional
    public PostResponse create(Long authorId, CreatePostRequest request) {
        var author = userService.getRequiredUser(authorId);
        var kind = request.kind() == null ? PostKind.FEED : request.kind();
        var primaryMediaUrl = primaryMediaUrl(request);
        var post = postRepository.save(new Post(
                author,
                request.content().trim(),
                primaryMediaUrl,
                kind
        ));
        saveMedia(post, kind, request);
        saveHashtags(post, request.content());
        return mapper.toPost(post, authorId);
    }

    @Transactional(readOnly = true)
    public PostResponse get(Long currentUserId, Long postId) {
        return mapper.toPost(getRequiredPost(postId), currentUserId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> list(Long currentUserId, int page, int size) {
        var result = postRepository.findAllByOrderByCreatedAtDesc(pageRequest(page, size));
        return toPostPage(result, currentUserId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> reels(Long currentUserId, int page, int size) {
        var result = postRepository.findByKindOrderByCreatedAtDesc(PostKind.REEL, pageRequest(page, size));
        return toPostPage(result, currentUserId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> explore(Long currentUserId, int page, int size) {
        var result = postRepository.findAllByOrderByCreatedAtDesc(pageRequest(page, size));
        return toPostPage(result, currentUserId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> postsByUser(Long currentUserId, Long userId, int page, int size) {
        userService.getRequiredUser(userId);
        var result = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageRequest(page, size));
        return toPostPage(result, currentUserId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> feed(Long currentUserId, int page, int size) {
        var authorIds = new ArrayList<>(followRepository.findFollowingIds(currentUserId));
        authorIds.add(currentUserId);
        var result = postRepository.findByAuthorIdInOrderByCreatedAtDesc(authorIds, pageRequest(page, size));
        return toPostPage(result, currentUserId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> search(Long currentUserId, String query, int page, int size) {
        var result = postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(query, pageRequest(page, size));
        return toPostPage(result, currentUserId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> hashtag(Long currentUserId, String tag, int page, int size) {
        var normalizedTag = normalizeTag(tag);
        var posts = postHashtagRepository.findByTagOrderByPostCreatedAtDesc(normalizedTag).stream()
                .map(PostHashtag::getPost)
                .distinct()
                .toList();
        return toPostPage(posts, currentUserId, page, size);
    }

    @Transactional
    public PostResponse like(Long userId, Long postId) {
        var user = userService.getRequiredUser(userId);
        var post = getRequiredPost(postId);

        if (!postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            postLikeRepository.save(new PostLike(post, user));
            notificationService.create(
                    post.getAuthor(),
                    user,
                    NotificationType.LIKE,
                    user.getDisplayName() + " liked your post",
                    "POST",
                    post.getId()
            );
        }

        return mapper.toPost(post, userId);
    }

    @Transactional
    public PostResponse unlike(Long userId, Long postId) {
        var post = getRequiredPost(postId);
        postLikeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(postLikeRepository::delete);
        return mapper.toPost(post, userId);
    }

    @Transactional
    public PostResponse save(Long userId, Long postId) {
        var user = userService.getRequiredUser(userId);
        var post = getRequiredPost(postId);
        if (!savedPostRepository.existsByPostIdAndUserId(postId, userId)) {
            savedPostRepository.save(new SavedPost(post, user));
        }
        return mapper.toPost(post, userId);
    }

    @Transactional
    public PostResponse unsave(Long userId, Long postId) {
        var post = getRequiredPost(postId);
        savedPostRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(savedPostRepository::delete);
        return mapper.toPost(post, userId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> saved(Long userId, int page, int size) {
        var result = savedPostRepository.findByUserIdOrderByCreatedAtDesc(userId, pageRequest(page, size));
        return new PageResponse<>(
                result.map(savedPost -> mapper.toPost(savedPost.getPost(), userId)).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional
    public CommentResponse comment(Long userId, Long postId, CommentRequest request) {
        var user = userService.getRequiredUser(userId);
        var post = getRequiredPost(postId);
        var comment = commentRepository.save(new Comment(post, user, request.content().trim()));

        notificationService.create(
                post.getAuthor(),
                user,
                NotificationType.COMMENT,
                user.getDisplayName() + " commented on your post",
                "POST",
                post.getId()
        );

        return mapper.toComment(comment);
    }

    @Transactional(readOnly = true)
    public java.util.List<CommentResponse> comments(Long postId) {
        getRequiredPost(postId);
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(mapper::toComment)
                .toList();
    }

    private Post getRequiredPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Post not found"));
    }

    private PageRequest pageRequest(int page, int size) {
        return PageRequest.of(Math.max(0, page), Math.min(Math.max(size, 1), 50));
    }

    private PageResponse<PostResponse> toPostPage(org.springframework.data.domain.Page<Post> result, Long currentUserId) {
        return new PageResponse<>(
                result.map(post -> mapper.toPost(post, currentUserId)).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private PageResponse<PostResponse> toPostPage(List<Post> posts, Long currentUserId, int page, int size) {
        var safePage = Math.max(0, page);
        var safeSize = Math.min(Math.max(size, 1), 50);
        var fromIndex = Math.min(safePage * safeSize, posts.size());
        var toIndex = Math.min(fromIndex + safeSize, posts.size());
        var items = posts.subList(fromIndex, toIndex).stream()
                .map(post -> mapper.toPost(post, currentUserId))
                .toList();
        var totalPages = posts.isEmpty() ? 0 : (int) Math.ceil((double) posts.size() / safeSize);
        return new PageResponse<>(items, safePage, safeSize, posts.size(), totalPages);
    }

    private void saveMedia(Post post, PostKind postKind, CreatePostRequest request) {
        var mediaItems = request.media() == null ? List.<MediaItemRequest>of() : request.media();
        if (mediaItems.isEmpty() && !isBlank(request.mediaUrl())) {
            postMediaRepository.save(new PostMedia(
                    post,
                    request.mediaUrl().trim(),
                    inferMediaKind(request.mediaUrl(), postKind),
                    0,
                    null
            ));
            return;
        }

        for (int index = 0; index < mediaItems.size(); index++) {
            var mediaItem = mediaItems.get(index);
            postMediaRepository.save(new PostMedia(
                    post,
                    mediaItem.url().trim(),
                    mediaItem.kind() == null ? inferMediaKind(mediaItem.url(), postKind) : mediaItem.kind(),
                    index,
                    blankToNull(mediaItem.altText())
            ));
        }
    }

    private void saveHashtags(Post post, String text) {
        extractHashtags(text).forEach(tag -> postHashtagRepository.save(new PostHashtag(post, tag)));
    }

    private List<String> extractHashtags(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        var tags = new LinkedHashSet<String>();
        var matcher = HASHTAG_PATTERN.matcher(text);
        while (matcher.find()) {
            tags.add(normalizeTag(matcher.group(1)));
        }
        return List.copyOf(tags);
    }

    private String normalizeTag(String tag) {
        return tag == null ? "" : tag.replaceFirst("^#", "").trim().toLowerCase();
    }

    private String primaryMediaUrl(CreatePostRequest request) {
        if (!isBlank(request.mediaUrl())) {
            return request.mediaUrl().trim();
        }
        if (request.media() != null && !request.media().isEmpty()) {
            return request.media().get(0).url().trim();
        }
        return null;
    }

    private MediaKind inferMediaKind(String mediaUrl, PostKind postKind) {
        if (postKind == PostKind.REEL) {
            return MediaKind.VIDEO;
        }
        var normalizedUrl = mediaUrl == null ? "" : mediaUrl.toLowerCase();
        if (normalizedUrl.endsWith(".mp4") || normalizedUrl.endsWith(".mov") || normalizedUrl.endsWith(".webm")) {
            return MediaKind.VIDEO;
        }
        return MediaKind.IMAGE;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
