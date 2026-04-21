package com.example.messenger.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallSignal {

    private Long callerId;
    private String callerName;
    private Long receiverId;
    private Long chatId;

    private CallSignalType type;
    private CallType callType;

    private String sdp;
    private String candidate;
    private String sdpMid;
    private Integer sdpMLineIndex;

    public enum CallSignalType {
        OFFER,
        ANSWER,
        ICE_CANDIDATE,
        CALL_INITIATED,
        CALL_ACCEPTED,
        CALL_REJECTED,
        CALL_ENDED,
        CALL_BUSY
    }

    public enum CallType {
        AUDIO,
        VIDEO
    }
}
