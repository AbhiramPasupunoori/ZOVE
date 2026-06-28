package com.zove.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zove.app.model.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByUserOneIdAndUserTwoId(Long userOneId, Long userTwoId);

    List<Conversation> findByUserOneIdOrUserTwoIdOrderByUpdatedAtDesc(Long userOneId, Long userTwoId);
}
