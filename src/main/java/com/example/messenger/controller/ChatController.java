package com.example.messenger.controller;

import com.example.messenger.dto.*;
import com.example.messenger.model.User;
import com.example.messenger.service.ChatService;
import com.example.messenger.service.OnlineStatusService;
import com.example.messenger.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final OnlineStatusService onlineStatusService;
    private final SimpMessagingTemplate messagingTemplate;

    // REST API endpoints

    @GetMapping("/api/chats")
    public ResponseEntity<List<ChatDTO>> getMyChats(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getUserChats(user));
    }

    @PostMapping("/api/chats")
    public ResponseEntity<ChatDTO> createChat(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateChatRequest request
    ) {
        return ResponseEntity.ok(chatService.createChat(user, request));
    }

    @GetMapping("/api/chats/private/{userId}")
    public ResponseEntity<ChatDTO> getOrCreatePrivateChat(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(chatService.getOrCreatePrivateChat(user, userId));
    }

    @GetMapping("/api/chats/{chatId}/messages")
    public ResponseEntity<List<MessageDTO>> getChatMessages(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        if (!chatService.isParticipant(chatId, user.getId())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(chatService.getChatMessages(chatId, page, size));
    }

    @GetMapping("/api/chats/{chatId}/messages/all")
    public ResponseEntity<List<MessageDTO>> getAllChatMessages(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatId
    ) {
        if (!chatService.isParticipant(chatId, user.getId())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(chatService.getAllChatMessages(chatId));
    }

    @PostMapping("/api/chats/{chatId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatId
    ) {
        chatService.markAsRead(chatId, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @AuthenticationPrincipal User user,
            @PathVariable Long messageId
    ) {
        chatService.deleteMessage(messageId, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/messages/{messageId}")
    public ResponseEntity<MessageDTO> editMessage(
            @AuthenticationPrincipal User user,
            @PathVariable Long messageId,
            @RequestBody Map<String, String> body
    ) {
        String newContent = body.get("content");
        return ResponseEntity.ok(chatService.editMessage(messageId, user, newContent));
    }

    @GetMapping("/api/chats/{chatId}/search")
    public ResponseEntity<List<MessageDTO>> searchMessages(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatId,
            @RequestParam String query
    ) {
        if (!chatService.isParticipant(chatId, user.getId())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(chatService.searchMessages(chatId, query));
    }

    @PostMapping("/api/chats/{chatId}/participants")
    public ResponseEntity<Void> addParticipants(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatId,
            @RequestBody List<Long> userIds
    ) {
        chatService.addParticipants(chatId, user, userIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/chats/{chatId}/leave")
    public ResponseEntity<Void> leaveChat(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatId
    ) {
        chatService.leaveChat(chatId, user);
        return ResponseEntity.ok().build();
    }

    // WebSocket endpoints

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        User sender = userService.findByUsername(principal.getName());
        MessageDTO message = chatService.sendMessage(sender, request);

        // Отправляем сообщение всем участникам чата
        messagingTemplate.convertAndSend("/topic/chat/" + request.getChatId(), message);
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload Map<String, Object> payload, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long chatId = ((Number) payload.get("chatId")).longValue();
        boolean isTyping = (boolean) payload.get("isTyping");

        if (isTyping) {
            onlineStatusService.startTyping(user.getId(), chatId);
        } else {
            onlineStatusService.stopTyping(user.getId(), chatId);
        }
    }

    @MessageMapping("/chat.read")
    public void markMessagesRead(@Payload Map<String, Object> payload, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long chatId = ((Number) payload.get("chatId")).longValue();

        chatService.markAsRead(chatId, user);

        // Уведомляем о прочтении
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/read", Map.of(
                "userId", user.getId(),
                "chatId", chatId
        ));
    }
}
