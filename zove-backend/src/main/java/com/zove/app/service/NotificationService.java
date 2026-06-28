package com.zove.app.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.zove.app.dto.SocialDtos.CountResponse;
import com.zove.app.dto.SocialDtos.NotificationResponse;
import com.zove.app.dto.SocialDtos.PageResponse;
import com.zove.app.model.AppUser;
import com.zove.app.model.Notification;
import com.zove.app.model.NotificationType;
import com.zove.app.repository.NotificationRepository;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final DtoMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(
            NotificationRepository notificationRepository,
            DtoMapper mapper,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.notificationRepository = notificationRepository;
        this.mapper = mapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void create(AppUser recipient, AppUser actor, NotificationType type, String message, String targetType, Long targetId) {
        if (recipient.getId().equals(actor.getId())) {
            return;
        }

        var notification = notificationRepository.save(new Notification(
                recipient,
                actor,
                type,
                message,
                targetType,
                targetId
        ));
        messagingTemplate.convertAndSendToUser(
                recipient.getId().toString(),
                "/queue/notifications",
                mapper.toNotification(notification)
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> list(Long userId, int page, int size) {
        var result = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(
                userId,
                PageRequest.of(page, size)
        );
        return new PageResponse<>(
                result.map(mapper::toNotification).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public CountResponse unreadCount(Long userId) {
        return new CountResponse(notificationRepository.countByRecipientIdAndReadAtIsNull(userId));
    }

    @Transactional
    public NotificationResponse markRead(Long userId, Long notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Notification not found"));
        if (!notification.getRecipient().getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "Notification does not belong to current user");
        }
        notification.markRead();
        return mapper.toNotification(notification);
    }

    @Transactional
    public CountResponse markAllRead(Long userId) {
        var unread = notificationRepository.findByRecipientIdAndReadAtIsNull(userId);
        unread.forEach(Notification::markRead);
        return new CountResponse(unread.size());
    }
}
