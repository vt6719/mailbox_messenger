package com.example.messenger.dto;

import com.example.messenger.model.User;
import com.example.messenger.model.UserSettings;
import com.example.messenger.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String displayName;
    private String email;
    private String avatarUrl;
    private String bio;
    private UserStatus status;
    private LocalDateTime lastSeen;
    private UserSettings settings;
    private LocalDateTime createdAt;

    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .status(user.getStatus())
                .lastSeen(user.getLastSeen())
                .settings(user.getSettings())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static UserDTO fromEntityPublic(User user) {
        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .build();

        if (user.getSettings() != null && user.getSettings().isShowOnlineStatus()) {
            dto.setStatus(user.getStatus());
            dto.setLastSeen(user.getLastSeen());
        }

        return dto;
    }
}
