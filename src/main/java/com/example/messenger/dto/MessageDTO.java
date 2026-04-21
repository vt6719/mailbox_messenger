package com.example.messenger.dto;

import com.example.messenger.model.ChatMessage;
import com.example.messenger.model.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    private Long chatId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String content;
    private MessageType type;
    private String mediaUrl;
    private String mediaFileName;
    private Long mediaFileSize;
    private Integer mediaDuration;
    private Integer mediaWidth;
    private Integer mediaHeight;
    private LocalDateTime timestamp;
    private boolean isRead;
    private boolean isEdited;
    private boolean isDeleted;
    private MessageDTO replyTo;

    public static MessageDTO fromEntity(ChatMessage message) {
        if (message == null) return null;

        MessageDTO dto = MessageDTO.builder()
                .id(message.getId())
                .chatId(message.getChat() != null ? message.getChat().getId() : null)
                .senderId(message.getSender() != null ? message.getSender().getId() : null)
                .senderName(message.getSender() != null ? message.getSender().getDisplayName() : message.getSenderName())
                .senderAvatar(message.getSender() != null ? message.getSender().getAvatarUrl() : null)
                .content(message.getContent())
                .type(message.getType())
                .mediaUrl(message.getMediaUrl())
                .mediaFileName(message.getMediaFileName())
                .mediaFileSize(message.getMediaFileSize())
                .mediaDuration(message.getMediaDuration())
                .mediaWidth(message.getMediaWidth())
                .mediaHeight(message.getMediaHeight())
                .timestamp(message.getTimestamp())
                .isRead(message.isRead())
                .isEdited(message.isEdited())
                .isDeleted(message.isDeleted())
                .build();

        if (message.getReplyTo() != null) {
            dto.setReplyTo(fromEntity(message.getReplyTo()));
        }

        return dto;
    }
}
