package com.portfolio.notification.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserRegisteredEvent {
    private Long userId;
    private String email;
    private String fullName;
    private LocalDateTime registeredAt;
}
