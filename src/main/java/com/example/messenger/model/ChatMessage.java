package com.example.messenger.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageType type = MessageType.TEXT;

    private String mediaUrl;

    private String mediaFileName;

    private Long mediaFileSize;

    private Integer mediaDuration;

    private Integer mediaWidth;

    private Integer mediaHeight;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Builder.Default
    private boolean isRead = false;

    @Builder.Default
    private boolean isEdited = false;

    @Builder.Default
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id")
    private ChatMessage replyTo;

    // Для совместимости со старым кодом (временный sender как String)
    @Transient
    private String senderName;

    public String getSenderName() {
        if (senderName != null) return senderName;
        return sender != null ? sender.getDisplayName() : null;
    }
}
