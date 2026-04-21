package com.example.messenger.repository;

import com.example.messenger.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE m.chat.id = :chatId AND m.isDeleted = false ORDER BY m.timestamp DESC")
    Page<ChatMessage> findByChatId(@Param("chatId") Long chatId, Pageable pageable);

    @Query("SELECT m FROM ChatMessage m WHERE m.chat.id = :chatId AND m.isDeleted = false ORDER BY m.timestamp ASC")
    List<ChatMessage> findAllByChatIdOrderByTimestamp(@Param("chatId") Long chatId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chat.id = :chatId AND m.isRead = false AND m.sender.id != :userId")
    long countUnreadMessages(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chat.id = :chatId AND m.sender.id != :userId")
    void markAllAsRead(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query("SELECT m FROM ChatMessage m WHERE m.chat.id = :chatId AND " +
           "LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%')) AND m.isDeleted = false")
    List<ChatMessage> searchInChat(@Param("chatId") Long chatId, @Param("query") String query);

    @Query("SELECT m FROM ChatMessage m WHERE m.chat.id = :chatId ORDER BY m.timestamp DESC LIMIT 1")
    ChatMessage findLastMessageInChat(@Param("chatId") Long chatId);
}
