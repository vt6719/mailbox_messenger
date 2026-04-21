package com.example.messenger.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettings {

    @Builder.Default
    private boolean showOnlineStatus = true;

    @Builder.Default
    private boolean readReceipts = true;

    @Builder.Default
    private boolean typingIndicator = true;

    @Builder.Default
    private String theme = "light";

    @Builder.Default
    private String accentColor = "#4A6FFF";

    @Builder.Default
    private boolean notificationsEnabled = true;

    @Builder.Default
    private boolean soundEnabled = true;
}
