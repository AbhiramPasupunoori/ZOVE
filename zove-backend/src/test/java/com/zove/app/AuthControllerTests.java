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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

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
    void registrationReturnsJwtAndJwtCanReadCurrentUser() throws Exception {
        var registration = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Ada Lovelace",
                                  "username": "ada_l",
                                  "email": "ADA@ZOVE.TEST",
                                  "password": "zovePass123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.displayName").value("Ada Lovelace"))
                .andExpect(jsonPath("$.user.email").value("ada@zove.test"))
                .andReturn();

        String token = JsonPath.read(registration.getResponse().getContentAsString(), "$.token");

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ada_l"))
                .andExpect(jsonPath("$.email").value("ada@zove.test"));
    }

    @Test
    void loginReturnsJwtForExistingUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Grace Hopper",
                                  "username": "grace_h",
                                  "email": "grace@zove.test",
                                  "password": "zovePass123"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "grace@zove.test",
                                  "password": "zovePass123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.username").value("grace_h"));
    }

    @Test
    void currentUserRequiresJwt() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
