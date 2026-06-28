package com.zove.app;

import com.jayway.jsonpath.JsonPath;
import com.zove.app.repository.ChatMessageRepository;
import com.zove.app.repository.CommentRepository;
import com.zove.app.repository.ConversationRepository;
import com.zove.app.repository.FollowRepository;
import com.zove.app.repository.NotificationRepository;
import com.zove.app.repository.PostLikeRepository;
import com.zove.app.repository.PostHashtagRepository;
import com.zove.app.repository.PostMediaRepository;
import com.zove.app.repository.PostRepository;
import com.zove.app.repository.SavedPostRepository;
import com.zove.app.repository.StoryRepository;
import com.zove.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SocialFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private SavedPostRepository savedPostRepository;

    @Autowired
    private PostHashtagRepository postHashtagRepository;

    @Autowired
    private PostMediaRepository postMediaRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearData() {
        chatMessageRepository.deleteAll();
        conversationRepository.deleteAll();
        notificationRepository.deleteAll();
        commentRepository.deleteAll();
        postLikeRepository.deleteAll();
        savedPostRepository.deleteAll();
        postHashtagRepository.deleteAll();
        postMediaRepository.deleteAll();
        storyRepository.deleteAll();
        postRepository.deleteAll();
        followRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void socialBackendFlowWorksEndToEnd() throws Exception {
        var ada = register("Ada Lovelace", "ada_l", "ada@zove.test");
        var grace = register("Grace Hopper", "grace_h", "grace@zove.test");

        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", bearer(ada.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Ada L.",
                                  "bio": "Building ZOVE",
                                  "avatarUrl": "/uploads/ada.png"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value("Building ZOVE"));

        var postResponse = mockMvc.perform(post("/api/posts")
                        .header("Authorization", bearer(ada.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "The first complete backend post is live.",
                                  "mediaUrl": "/uploads/post.png"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("The first complete backend post is live."))
                .andReturn();
        Integer postId = JsonPath.read(postResponse.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/api/users/{userId}/follow", ada.id())
                        .header("Authorization", bearer(grace.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followed").value(true))
                .andExpect(jsonPath("$.followersCount").value(1));

        mockMvc.perform(get("/api/feed")
                        .header("Authorization", bearer(grace.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(postId));

        mockMvc.perform(post("/api/posts/{postId}/likes", postId)
                        .header("Authorization", bearer(grace.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likesCount").value(1))
                .andExpect(jsonPath("$.likedByCurrentUser").value(true));

        mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                        .header("Authorization", bearer(grace.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "This backend is ready for the feed."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("This backend is ready for the feed."));

        mockMvc.perform(get("/api/posts/{postId}/comments", postId)
                        .header("Authorization", bearer(ada.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author.username").value("grace_h"));

        mockMvc.perform(get("/api/notifications/unread-count")
                        .header("Authorization", bearer(ada.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));

        mockMvc.perform(get("/api/search?q=complete")
                        .header("Authorization", bearer(grace.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].id").value(postId));

        var messageResponse = mockMvc.perform(post("/api/messages")
                        .header("Authorization", bearer(ada.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipientId": %d,
                                  "content": "Private messaging is online."
                                }
                                """.formatted(grace.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Private messaging is online."))
                .andReturn();
        Integer conversationId = JsonPath.read(messageResponse.getResponse().getContentAsString(), "$.conversationId");

        mockMvc.perform(get("/api/conversations")
                        .header("Authorization", bearer(grace.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(conversationId))
                .andExpect(jsonPath("$[0].unreadCount").value(1));

        mockMvc.perform(get("/api/conversations/{conversationId}/messages", conversationId)
                        .header("Authorization", bearer(grace.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].read").value(true))
                .andExpect(jsonPath("$[0].sender.username").value("ada_l"));
    }

    @Test
    void instagramLikeMediaStorySaveAndHashtagFlowWorks() throws Exception {
        var ada = register("Ada Lovelace", "ada_l", "ada@zove.test");

        var carouselResponse = mockMvc.perform(post("/api/posts")
                        .header("Authorization", bearer(ada.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Carousel launch #zove #backend",
                                  "kind": "FEED",
                                  "media": [
                                    {
                                      "url": "/uploads/carousel-1.jpg",
                                      "kind": "IMAGE",
                                      "altText": "First carousel image"
                                    },
                                    {
                                      "url": "/uploads/carousel-2.jpg",
                                      "kind": "IMAGE",
                                      "altText": "Second carousel image"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.media.length()").value(2))
                .andExpect(jsonPath("$.hashtags[0]").value("zove"))
                .andReturn();
        Integer carouselPostId = JsonPath.read(carouselResponse.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/api/posts/{postId}/saves", carouselPostId)
                        .header("Authorization", bearer(ada.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.savedByCurrentUser").value(true));

        mockMvc.perform(get("/api/posts/saved")
                        .header("Authorization", bearer(ada.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(carouselPostId));

        mockMvc.perform(get("/api/hashtags/zove/posts")
                        .header("Authorization", bearer(ada.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(carouselPostId));

        var reelResponse = mockMvc.perform(post("/api/posts")
                        .header("Authorization", bearer(ada.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Short video drop #reels",
                                  "kind": "REEL",
                                  "mediaUrl": "/uploads/reel.mp4"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kind").value("REEL"))
                .andExpect(jsonPath("$.media[0].kind").value("VIDEO"))
                .andReturn();
        Integer reelPostId = JsonPath.read(reelResponse.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/posts/reels")
                        .header("Authorization", bearer(ada.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(reelPostId));

        mockMvc.perform(get("/api/explore/posts")
                        .header("Authorization", bearer(ada.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2));

        mockMvc.perform(post("/api/stories")
                        .header("Authorization", bearer(ada.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mediaUrl": "/uploads/story.jpg",
                                  "kind": "IMAGE",
                                  "caption": "Behind the scenes"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caption").value("Behind the scenes"));

        mockMvc.perform(get("/api/stories/feed")
                        .header("Authorization", bearer(ada.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mediaUrl").value("/uploads/story.jpg"));

        mockMvc.perform(get("/api/search?q=zove")
                        .header("Authorization", bearer(ada.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hashtags[0].tag").value("zove"));
    }

    private RegisteredUser register(String displayName, String username, String email) throws Exception {
        var response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "%s",
                                  "username": "%s",
                                  "email": "%s",
                                  "password": "zovePass123"
                                }
                                """.formatted(displayName, username, email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(response, "$.token");
        Integer id = JsonPath.read(response, "$.user.id");
        return new RegisteredUser(id.longValue(), token);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private record RegisteredUser(Long id, String token) {
    }
}
