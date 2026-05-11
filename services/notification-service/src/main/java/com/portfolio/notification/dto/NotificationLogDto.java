package com.portfolio.notification.dto;

import com.portfolio.notification.enums.NotificationStatus;
import com.portfolio.notification.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationLogDto {
    private Long id;
    private Long userId;
    private String recipientEmail;
    private String subject;
    private NotificationType type;
    private NotificationStatus status;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}
