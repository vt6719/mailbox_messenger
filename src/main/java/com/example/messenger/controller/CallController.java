package com.example.messenger.controller;

import com.example.messenger.model.CallSignal;
import com.example.messenger.model.User;
import com.example.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CallController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    @MessageMapping("/call.initiate")
    public void initiateCall(@Payload CallSignal signal, Principal principal) {
        User caller = userService.findByUsername(principal.getName());
        signal.setCallerId(caller.getId());
        signal.setCallerName(caller.getDisplayName());
        signal.setType(CallSignal.CallSignalType.CALL_INITIATED);

        // Отправляем получателю уведомление о входящем звонке
        messagingTemplate.convertAndSendToUser(
                signal.getReceiverId().toString(),
                "/queue/call",
                signal
        );
    }

    @MessageMapping("/call.accept")
    public void acceptCall(@Payload CallSignal signal, Principal principal) {
        signal.setType(CallSignal.CallSignalType.CALL_ACCEPTED);

        // Уведомляем звонящего о принятии звонка
        messagingTemplate.convertAndSendToUser(
                signal.getCallerId().toString(),
                "/queue/call",
                signal
        );
    }

    @MessageMapping("/call.reject")
    public void rejectCall(@Payload CallSignal signal, Principal principal) {
        signal.setType(CallSignal.CallSignalType.CALL_REJECTED);

        // Уведомляем звонящего об отклонении
        messagingTemplate.convertAndSendToUser(
                signal.getCallerId().toString(),
                "/queue/call",
                signal
        );
    }

    @MessageMapping("/call.end")
    public void endCall(@Payload CallSignal signal, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        signal.setType(CallSignal.CallSignalType.CALL_ENDED);

        // Определяем кому отправить уведомление
        Long targetUserId = user.getId().equals(signal.getCallerId())
                ? signal.getReceiverId()
                : signal.getCallerId();

        messagingTemplate.convertAndSendToUser(
                targetUserId.toString(),
                "/queue/call",
                signal
        );
    }

    @MessageMapping("/call.offer")
    public void handleOffer(@Payload CallSignal signal, Principal principal) {
        signal.setType(CallSignal.CallSignalType.OFFER);

        messagingTemplate.convertAndSendToUser(
                signal.getReceiverId().toString(),
                "/queue/call",
                signal
        );
    }

    @MessageMapping("/call.answer")
    public void handleAnswer(@Payload CallSignal signal, Principal principal) {
        signal.setType(CallSignal.CallSignalType.ANSWER);

        messagingTemplate.convertAndSendToUser(
                signal.getCallerId().toString(),
                "/queue/call",
                signal
        );
    }

    @MessageMapping("/call.ice-candidate")
    public void handleIceCandidate(@Payload CallSignal signal, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        signal.setType(CallSignal.CallSignalType.ICE_CANDIDATE);

        // Отправляем ICE candidate другому участнику
        Long targetUserId = user.getId().equals(signal.getCallerId())
                ? signal.getReceiverId()
                : signal.getCallerId();

        messagingTemplate.convertAndSendToUser(
                targetUserId.toString(),
                "/queue/call",
                signal
        );
    }
}
