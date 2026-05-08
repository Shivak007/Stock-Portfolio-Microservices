package com.portfolio.alertservice.entity;

import com.portfolio.alertservice.enums.AlertCondition;
import com.portfolio.alertservice.enums.AlertStatus;
import com.portfolio.alertservice.enums.AlertType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType;

    private String stockSymbol;

    private BigDecimal targetPrice;

    private Long portfolioId;

    private BigDecimal lossThresholdPercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_condition")
    @Builder.Default
    private AlertCondition condition = AlertCondition.ABOVE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AlertStatus status = AlertStatus.ACTIVE;

    private LocalDateTime lastTriggeredAt;

    @CreatedDate
    private LocalDateTime createdAt;
}
