package com.portfolio.pricefetcher.service;

import com.portfolio.pricefetcher.dto.response.CacheStatsDto;
import com.portfolio.pricefetcher.dto.response.PriceResponseDto;

import java.util.List;

public interface PriceService {
    PriceResponseDto getPrice(String symbol);
    List<PriceResponseDto> getBatchPrices(List<String> symbols);
    PriceResponseDto forceRefresh(String symbol);
    List<PriceResponseDto> getPriceHistory(String symbol, int days);
    CacheStatsDto getCacheStats();
    void refreshAllActivePrices();  // called by cron
}
