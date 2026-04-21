package com.example.messenger.dto;

import com.example.messenger.model.ChatType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRequest {

    @Size(max = 100, message = "Название чата не должно превышать 100 символов")
    private String name;

    private ChatType type = ChatType.PRIVATE;

    @NotEmpty(message = "Необходимо указать хотя бы одного участника")
    private List<Long> participantIds;
}
