package com.zove.app.controller;

import java.security.Principal;

import jakarta.validation.Valid;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import com.zove.app.dto.SocialDtos.SendMessageRequest;
import com.zove.app.service.MessageService;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Controller
public class RealtimeMessageController {

    private final MessageService messageService;

    public RealtimeMessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/messages.send")
    public void send(@Valid @Payload SendMessageRequest request, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "WebSocket user is not authenticated");
        }
        messageService.send(Long.valueOf(principal.getName()), request);
    }
}
