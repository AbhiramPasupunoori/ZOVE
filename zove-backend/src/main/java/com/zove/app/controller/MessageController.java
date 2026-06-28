package com.zove.app.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zove.app.dto.SocialDtos.ConversationResponse;
import com.zove.app.dto.SocialDtos.MessageResponse;
import com.zove.app.dto.SocialDtos.SendMessageRequest;
import com.zove.app.service.MessageService;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/conversations")
    public List<ConversationResponse> conversations(@AuthenticationPrincipal Jwt jwt) {
        return messageService.conversations(userId(jwt));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<MessageResponse> messages(@AuthenticationPrincipal Jwt jwt, @PathVariable Long conversationId) {
        return messageService.messages(userId(jwt), conversationId);
    }

    @PostMapping("/messages")
    public MessageResponse send(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody SendMessageRequest request) {
        return messageService.send(userId(jwt), request);
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
