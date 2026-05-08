package com.portfolio.pricefetcher.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheStatsDto {
    private long hits;
    private long misses;
    private long totalRequests;
    private double hitRatePercent;
    private long cachedSymbolsCount;
}
