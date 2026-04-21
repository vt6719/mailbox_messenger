package com.example.messenger.dto;

import com.example.messenger.model.UserSettings;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 3, max = 30, message = "Username должен быть от 3 до 30 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username может содержать только буквы, цифры и подчеркивания")
    private String username;

    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String displayName;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String bio;

    private UserSettings settings;
}
