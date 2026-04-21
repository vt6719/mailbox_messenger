package com.example.messenger.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username или email обязателен")
    private String usernameOrEmail;

    @NotBlank(message = "Пароль обязателен")
    private String password;
}
