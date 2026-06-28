package com.zove.app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zove.app.dto.SocialDtos.CountResponse;
import com.zove.app.dto.SocialDtos.NotificationResponse;
import com.zove.app.dto.SocialDtos.PageResponse;
import com.zove.app.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public PageResponse<NotificationResponse> list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return notificationService.list(userId(jwt), page, size);
    }

    @GetMapping("/unread-count")
    public CountResponse unreadCount(@AuthenticationPrincipal Jwt jwt) {
        return notificationService.unreadCount(userId(jwt));
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markRead(@AuthenticationPrincipal Jwt jwt, @PathVariable Long notificationId) {
        return notificationService.markRead(userId(jwt), notificationId);
    }

    @PatchMapping("/read-all")
    public CountResponse markAllRead(@AuthenticationPrincipal Jwt jwt) {
        return notificationService.markAllRead(userId(jwt));
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
