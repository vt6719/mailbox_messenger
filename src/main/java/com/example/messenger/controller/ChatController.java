package com.example.messenger.controller;

import com.example.messenger.model.ChatMessage;
import com.example.messenger.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        repository.save(chatMessage);
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }
    
    @GetMapping("/api/messages")
    @ResponseBody
    public List<ChatMessage> getHistory() {
        return repository.findAll();
    }
}