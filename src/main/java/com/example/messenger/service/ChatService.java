package com.example.messenger.service;

import com.example.messenger.dto.*;
import com.example.messenger.model.*;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.MessageRepository;
import com.example.messenger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<ChatDTO> getUserChats(User user) {
        return chatRepository.findByParticipantId(user.getId()).stream()
                .map(chat -> {
                    long unreadCount = messageRepository.countUnreadMessages(chat.getId(), user.getId());
                    return ChatDTO.fromEntityWithUnread(chat, user, unreadCount);
                })
                .collect(Collectors.toList());
    }

    public Chat findById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Чат не найден"));
    }

    @Transactional
    public ChatDTO createChat(User creator, CreateChatRequest request) {
        Set<User> participants = new HashSet<>();
        participants.add(creator);

        List<User> otherParticipants = userRepository.findAllByIds(request.getParticipantIds());
        participants.addAll(otherParticipants);

        if (request.getType() == ChatType.PRIVATE && participants.size() == 2) {
            Long otherUserId = request.getParticipantIds().get(0);
            return chatRepository.findPrivateChatBetweenUsers(creator.getId(), otherUserId, ChatType.PRIVATE)
                    .map(existingChat -> ChatDTO.fromEntity(existingChat, creator))
                    .orElseGet(() -> createNewChat(creator, request, participants));
        }

        return createNewChat(creator, request, participants);
    }

    private ChatDTO createNewChat(User creator, CreateChatRequest request, Set<User> participants) {
        Chat chat = Chat.builder()
                .name(request.getName())
                .type(request.getType())
                .participants(participants)
                .creator(creator)
                .createdAt(LocalDateTime.now())
                .build();

        chatRepository.save(chat);
        return ChatDTO.fromEntity(chat, creator);
    }

    @Transactional
    public ChatDTO getOrCreatePrivateChat(User user, Long otherUserId) {
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return chatRepository.findPrivateChatBetweenUsers(user.getId(), otherUserId, ChatType.PRIVATE)
                .map(chat -> ChatDTO.fromEntity(chat, user))
                .orElseGet(() -> {
                    CreateChatRequest request = CreateChatRequest.builder()
                            .type(ChatType.PRIVATE)
                            .participantIds(List.of(otherUserId))
                            .build();
                    return createChat(user, request);
                });
    }

    public List<MessageDTO> getChatMessages(Long chatId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = messageRepository.findByChatId(chatId, pageable);
        return messages.getContent().stream()
                .map(MessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getAllChatMessages(Long chatId) {
        return messageRepository.findAllByChatIdOrderByTimestamp(chatId).stream()
                .map(MessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDTO sendMessage(User sender, SendMessageRequest request) {
        Chat chat = findById(request.getChatId());

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .chat(chat)
                .content(request.getContent())
                .type(request.getType() != null ? request.getType() : MessageType.TEXT)
                .mediaUrl(request.getMediaUrl())
                .mediaFileName(request.getMediaFileName())
                .mediaFileSize(request.getMediaFileSize())
                .mediaDuration(request.getMediaDuration())
                .mediaWidth(request.getMediaWidth())
                .mediaHeight(request.getMediaHeight())
                .timestamp(LocalDateTime.now())
                .build();

        if (request.getReplyToId() != null) {
            messageRepository.findById(request.getReplyToId())
                    .ifPresent(message::setReplyTo);
        }

        messageRepository.save(message);

        chat.setLastMessageAt(message.getTimestamp());
        chat.setLastMessagePreview(getMessagePreview(message));
        chatRepository.save(chat);

        return MessageDTO.fromEntity(message);
    }

    private String getMessagePreview(ChatMessage message) {
        return switch (message.getType()) {
            case TEXT -> message.getContent() != null && message.getContent().length() > 50
                    ? message.getContent().substring(0, 50) + "..."
                    : message.getContent();
            case VOICE -> "Голосовое сообщение";
            case IMAGE -> "Фотография";
            case VIDEO -> "Видео";
            case FILE -> "Файл: " + message.getMediaFileName();
            case SYSTEM -> message.getContent();
        };
    }

    @Transactional
    public void markAsRead(Long chatId, User user) {
        messageRepository.markAllAsRead(chatId, user.getId());
    }

    @Transactional
    public void deleteMessage(Long messageId, User user) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Сообщение не найдено"));

        if (!message.getSender().getId().equals(user.getId())) {
            throw new RuntimeException("Нет прав на удаление сообщения");
        }

        message.setDeleted(true);
        message.setContent("Сообщение удалено");
        messageRepository.save(message);
    }

    @Transactional
    public MessageDTO editMessage(Long messageId, User user, String newContent) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Сообщение не найдено"));

        if (!message.getSender().getId().equals(user.getId())) {
            throw new RuntimeException("Нет прав на редактирование сообщения");
        }

        if (message.getType() != MessageType.TEXT) {
            throw new RuntimeException("Редактирование доступно только для текстовых сообщений");
        }

        message.setContent(newContent);
        message.setEdited(true);
        messageRepository.save(message);

        return MessageDTO.fromEntity(message);
    }

    public List<MessageDTO> searchMessages(Long chatId, String query) {
        return messageRepository.searchInChat(chatId, query).stream()
                .map(MessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public boolean isParticipant(Long chatId, Long userId) {
        Chat chat = findById(chatId);
        return chat.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(userId));
    }

    @Transactional
    public void addParticipants(Long chatId, User user, List<Long> userIds) {
        Chat chat = findById(chatId);

        if (chat.getType() != ChatType.GROUP) {
            throw new RuntimeException("Нельзя добавить участников в приватный чат");
        }

        if (!chat.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("Только создатель может добавлять участников");
        }

        List<User> newParticipants = userRepository.findAllByIds(userIds);
        chat.getParticipants().addAll(newParticipants);
        chatRepository.save(chat);
    }

    @Transactional
    public void leaveChat(Long chatId, User user) {
        Chat chat = findById(chatId);

        if (chat.getType() != ChatType.GROUP) {
            throw new RuntimeException("Нельзя покинуть приватный чат");
        }

        chat.getParticipants().remove(user);
        chatRepository.save(chat);
    }
}
