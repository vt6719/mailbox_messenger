package com.example.messenger.dto;

import com.example.messenger.model.Chat;
import com.example.messenger.model.ChatType;
import com.example.messenger.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {

    private Long id;
    private String name;
    private ChatType type;
    private String avatarUrl;
    private List<UserDTO> participants;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private String lastMessagePreview;
    private long unreadCount;

    public static ChatDTO fromEntity(Chat chat, User currentUser) {
        String chatName = chat.getName();
        String chatAvatar = chat.getAvatarUrl();

        if (chat.getType() == ChatType.PRIVATE && chat.getParticipants() != null) {
            User otherUser = chat.getParticipants().stream()
                    .filter(u -> !u.getId().equals(currentUser.getId()))
                    .findFirst()
                    .orElse(null);

            if (otherUser != null) {
                chatName = otherUser.getDisplayName();
                chatAvatar = otherUser.getAvatarUrl();
            }
        }

        return ChatDTO.builder()
                .id(chat.getId())
                .name(chatName)
                .type(chat.getType())
                .avatarUrl(chatAvatar)
                .participants(chat.getParticipants() != null ?
                        chat.getParticipants().stream()
                                .map(UserDTO::fromEntityPublic)
                                .collect(Collectors.toList()) : List.of())
                .createdAt(chat.getCreatedAt())
                .lastMessageAt(chat.getLastMessageAt())
                .lastMessagePreview(chat.getLastMessagePreview())
                .build();
    }

    public static ChatDTO fromEntityWithUnread(Chat chat, User currentUser, long unreadCount) {
        ChatDTO dto = fromEntity(chat, currentUser);
        dto.setUnreadCount(unreadCount);
        return dto;
    }
}
