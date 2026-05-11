package com.portfolio.pricefetcher.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "price_cache",
        indexes = {
                @Index(
                        name = "idx_price_cache_symbol",
                        columnList = "symbol"
                )
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal currentPrice;

    @Column(precision = 15, scale = 4)
    private BigDecimal previousPrice;

    @Column(precision = 8, scale = 4)
    private BigDecimal changePercent;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
