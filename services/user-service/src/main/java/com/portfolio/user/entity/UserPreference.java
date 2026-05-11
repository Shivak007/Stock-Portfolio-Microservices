package com.portfolio.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "email_notifications")
    private boolean emailNotifications;

    @Column(name = "price_alert_email")
    private boolean priceAlertEmail;

    @Column(name = "daily_summary_email")
    private boolean dailySummaryEmail;

    // Used by report-service and notification-service
    // to format currency values in reports/emails
    @Column(name = "preferred_currency")
    private String preferredCurrency;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
