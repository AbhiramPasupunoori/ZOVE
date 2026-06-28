package com.zove.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zove.app.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    Optional<ChatMessage> findTopByConversationIdOrderByCreatedAtDesc(Long conversationId);

    List<ChatMessage> findByConversationIdAndRecipientIdAndReadAtIsNull(Long conversationId, Long recipientId);

    long countByRecipientIdAndReadAtIsNull(Long recipientId);

    long countByConversationIdAndRecipientIdAndReadAtIsNull(Long conversationId, Long recipientId);
}
