package com.portfolio.notification.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlertTriggeredEvent {
    private Long alertId;
    private Long userId;
    private String email;
    private String stockSymbol;
    private String alertType;
    private String condition;
    private BigDecimal targetPrice;
    private BigDecimal currentPrice;
    private LocalDateTime triggeredAt;
}
