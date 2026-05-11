package com.portfolio.notification.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailySummaryEvent {
    private Long userId;
    private String email;
    private String fullName;
    private BigDecimal totalCurrentValue;
    private BigDecimal totalGainLoss;
    private BigDecimal totalGainLossPercent;
    private LocalDateTime generatedAt;
}
