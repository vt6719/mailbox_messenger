package com.example.messenger.repository;

import com.example.messenger.model.Chat;
import com.example.messenger.model.ChatType;
import com.example.messenger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.id = :userId ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<Chat> findByParticipantId(@Param("userId") Long userId);

    @Query("SELECT c FROM Chat c JOIN c.participants p1 JOIN c.participants p2 " +
           "WHERE c.type = :type AND p1.id = :user1Id AND p2.id = :user2Id")
    Optional<Chat> findPrivateChatBetweenUsers(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id,
            @Param("type") ChatType type
    );

    @Query("SELECT c FROM Chat c WHERE c.type = 'GROUP' AND LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Chat> searchGroupChats(@Param("query") String query);

    @Query("SELECT COUNT(p) FROM Chat c JOIN c.participants p WHERE c.id = :chatId")
    long countParticipants(@Param("chatId") Long chatId);
}
