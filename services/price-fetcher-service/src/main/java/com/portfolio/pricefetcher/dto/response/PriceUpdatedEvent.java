package com.portfolio.pricefetcher.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceUpdatedEvent {
    private String symbol;
    private BigDecimal currentPrice;
    private BigDecimal previousPrice;
    private BigDecimal changePercent;
    private LocalDateTime updatedAt;
}
