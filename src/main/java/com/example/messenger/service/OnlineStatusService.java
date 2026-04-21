package com.example.messenger.service;

import com.example.messenger.model.User;
import com.example.messenger.model.UserStatus;
import com.example.messenger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OnlineStatusService {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final Set<Long> onlineUsers = ConcurrentHashMap.newKeySet();
    private final Map<Long, Set<Long>> typingUsers = new ConcurrentHashMap<>();

    public void userConnected(Long userId) {
        onlineUsers.add(userId);
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(UserStatus.ONLINE);
            userRepository.save(user);
            broadcastStatusChange(userId, UserStatus.ONLINE);
        });
    }

    public void userDisconnected(Long userId) {
        onlineUsers.remove(userId);
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(UserStatus.OFFLINE);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
            broadcastStatusChange(userId, UserStatus.OFFLINE);
        });
    }

    public boolean isUserOnline(Long userId) {
        return onlineUsers.contains(userId);
    }

    public void startTyping(Long userId, Long chatId) {
        typingUsers.computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet()).add(userId);
        broadcastTypingStatus(chatId, userId, true);
    }

    public void stopTyping(Long userId, Long chatId) {
        Set<Long> chatTypingUsers = typingUsers.get(chatId);
        if (chatTypingUsers != null) {
            chatTypingUsers.remove(userId);
        }
        broadcastTypingStatus(chatId, userId, false);
    }

    public Set<Long> getTypingUsers(Long chatId) {
        return typingUsers.getOrDefault(chatId, Set.of());
    }

    private void broadcastStatusChange(Long userId, UserStatus status) {
        messagingTemplate.convertAndSend("/topic/status", Map.of(
                "userId", userId,
                "status", status.name(),
                "lastSeen", LocalDateTime.now().toString()
        ));
    }

    private void broadcastTypingStatus(Long chatId, Long userId, boolean isTyping) {
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/typing", Map.of(
                "userId", userId,
                "isTyping", isTyping
        ));
    }
}
