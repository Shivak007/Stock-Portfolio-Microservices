package com.portfolio.pricefetcher.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "price_history",
        indexes = {
                @Index(
                        name = "idx_symbol_price_date",
                        columnList = "symbol, priceDate"
                ),
                @Index(
                        name = "idx_symbol_recorded_at",
                        columnList = "symbol, recordedAt"
                )
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDate priceDate;

    private LocalDateTime recordedAt;

    @PrePersist
    public void setRecordedAt() {
        this.recordedAt = LocalDateTime.now();
    }
}
