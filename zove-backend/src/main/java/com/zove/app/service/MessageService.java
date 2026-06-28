package com.zove.app.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.zove.app.dto.SocialDtos.ConversationResponse;
import com.zove.app.dto.SocialDtos.MessageResponse;
import com.zove.app.dto.SocialDtos.SendMessageRequest;
import com.zove.app.model.AppUser;
import com.zove.app.model.ChatMessage;
import com.zove.app.model.Conversation;
import com.zove.app.model.NotificationType;
import com.zove.app.repository.ChatMessageRepository;
import com.zove.app.repository.ConversationRepository;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class MessageService {

    private final UserService userService;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final DtoMapper mapper;

    public MessageService(
            UserService userService,
            ConversationRepository conversationRepository,
            ChatMessageRepository chatMessageRepository,
            NotificationService notificationService,
            SimpMessagingTemplate messagingTemplate,
            DtoMapper mapper
    ) {
        this.userService = userService;
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.mapper = mapper;
    }

    @Transactional
    public MessageResponse send(Long senderId, SendMessageRequest request) {
        if (senderId.equals(request.recipientId())) {
            throw new ResponseStatusException(BAD_REQUEST, "You cannot message yourself");
        }

        var sender = userService.getRequiredUser(senderId);
        var recipient = userService.getRequiredUser(request.recipientId());
        var conversation = getOrCreateConversation(sender, recipient);
        var message = chatMessageRepository.save(new ChatMessage(
                conversation,
                sender,
                recipient,
                request.content().trim()
        ));
        conversation.touch();

        var response = mapper.toMessage(message);
        messagingTemplate.convertAndSendToUser(recipient.getId().toString(), "/queue/messages", response);
        messagingTemplate.convertAndSendToUser(sender.getId().toString(), "/queue/messages", response);
        notificationService.create(
                recipient,
                sender,
                NotificationType.MESSAGE,
                sender.getDisplayName() + " sent you a message",
                "CONVERSATION",
                conversation.getId()
        );
        return response;
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> conversations(Long userId) {
        userService.getRequiredUser(userId);
        return conversationRepository.findByUserOneIdOrUserTwoIdOrderByUpdatedAtDesc(userId, userId).stream()
                .map(conversation -> mapper.toConversation(conversation, userId))
                .toList();
    }

    @Transactional
    public List<MessageResponse> messages(Long userId, Long conversationId) {
        var conversation = getConversationForUser(userId, conversationId);
        chatMessageRepository.findByConversationIdAndRecipientIdAndReadAtIsNull(conversationId, userId)
                .forEach(ChatMessage::markRead);
        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId()).stream()
                .map(mapper::toMessage)
                .toList();
    }

    private Conversation getOrCreateConversation(AppUser sender, AppUser recipient) {
        var userOne = sender.getId() < recipient.getId() ? sender : recipient;
        var userTwo = sender.getId() < recipient.getId() ? recipient : sender;
        return conversationRepository.findByUserOneIdAndUserTwoId(userOne.getId(), userTwo.getId())
                .orElseGet(() -> conversationRepository.save(new Conversation(userOne, userTwo)));
    }

    private Conversation getConversationForUser(Long userId, Long conversationId) {
        var conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Conversation not found"));
        if (!conversation.getUserOne().getId().equals(userId) && !conversation.getUserTwo().getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "Conversation does not belong to current user");
        }
        return conversation;
    }
}
