package com.example.messenger.dto;

import com.example.messenger.model.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    private Long chatId;
    private String content;
    private MessageType type = MessageType.TEXT;
    private String mediaUrl;
    private String mediaFileName;
    private Long mediaFileSize;
    private Integer mediaDuration;
    private Integer mediaWidth;
    private Integer mediaHeight;
    private Long replyToId;
}
