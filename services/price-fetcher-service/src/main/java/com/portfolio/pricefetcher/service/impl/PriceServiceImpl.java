package com.portfolio.pricefetcher.service.impl;

import java.util.Optional;
import com.portfolio.pricefetcher.client.FinnhubClient;
import com.portfolio.pricefetcher.client.PortfolioFeignClient;
import com.portfolio.pricefetcher.dto.response.CacheStatsDto;
import com.portfolio.pricefetcher.dto.response.PriceResponseDto;
import com.portfolio.pricefetcher.dto.response.PriceUpdatedEvent;
import com.portfolio.pricefetcher.entity.PriceCache;
import com.portfolio.pricefetcher.entity.PriceHistory;
import com.portfolio.pricefetcher.exception.custom.ResourceNotFoundException;
import com.portfolio.pricefetcher.messaging.PriceEventPublisher;
import com.portfolio.pricefetcher.repository.PriceCacheRepository;
import com.portfolio.pricefetcher.repository.PriceHistoryRepository;
import com.portfolio.pricefetcher.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceServiceImpl implements PriceService {

    private static final String STOCK_PRICES_CACHE_NAME = "stockPrices";
    private static final String STOCK_PRICES_CACHE_KEY_PATTERN = STOCK_PRICES_CACHE_NAME + "::*";
    private final StringRedisTemplate stringRedisTemplate;
    private final FinnhubClient finnhubClient;
    private final PriceCacheRepository priceCacheRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final PortfolioFeignClient portfolioFeignClient;
    private final PriceEventPublisher priceEventPublisher;
    private final CacheManager cacheManager;

    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();

    @Override
    public PriceResponseDto getPrice(String symbol) {

        String normalizedSymbol = normalizeSymbol(symbol);
        PriceResponseDto cachedPrice = getCachedPrice(normalizedSymbol);

        if (cachedPrice != null) {
            cacheHits.incrementAndGet();
            log.debug("Cache HIT for symbol: {}", normalizedSymbol);
            return withCacheFlag(cachedPrice, true);
        }

        cacheMisses.incrementAndGet();
        log.debug("Cache MISS for symbol: {}. Fetching from external API.", normalizedSymbol);

        PriceResponseDto priceDto =
                finnhubClient.fetchPrice(normalizedSymbol);

        persistToDb(priceDto);
        putCachedPrice(normalizedSymbol, priceDto);

        return priceDto;
    }

    @Override
    public List<PriceResponseDto> getBatchPrices(List<String> symbols) {

        return symbols.stream()
                .map(this::getPrice)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PriceResponseDto forceRefresh(String symbol) {

        String normalizedSymbol = normalizeSymbol(symbol);

        log.info("Force-refreshing price for symbol: {}", normalizedSymbol);

        PriceResponseDto priceDto =
                finnhubClient.fetchPrice(normalizedSymbol);

        persistToDb(priceDto);
        putCachedPrice(normalizedSymbol, priceDto);

        publishPriceUpdatedEvent(priceDto);

        return priceDto;
    }

    @Override
    public List<PriceResponseDto> getPriceHistory(String symbol, int days) {

        String normalizedSymbol = normalizeSymbol(symbol);

        List<PriceHistory> history =
                priceHistoryRepository.findRecentBySymbol(
                        normalizedSymbol,
                        days
                );

        if (history.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No price history found for symbol: " + normalizedSymbol
            );
        }

        return history.stream()
                .map(h -> PriceResponseDto.builder()
                        .symbol(h.getSymbol())
                        .currentPrice(h.getPrice())
                        .updatedAt(h.getRecordedAt())
                        .fromCache(false)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CacheStatsDto getCacheStats() {

        long hits = cacheHits.get();
        long misses = cacheMisses.get();
        long total = hits + misses;
        double hitRate = total == 0 ? 0 : (double) hits / total * 100;
        long cachedSymbolsCount = countCachedSymbols();

        return CacheStatsDto.builder()
                .hits(hits)
                .misses(misses)
                .totalRequests(total)
                .hitRatePercent(hitRate)
                .cachedSymbolsCount(cachedSymbolsCount)
                .build();
    }

    @Override
    @Scheduled(fixedDelayString = "${price.fetch.interval:300000}")
    @Transactional
    public void refreshAllActivePrices() {

        log.info("Cron: Starting price refresh for all active symbols...");

        List<String> symbols =
                portfolioFeignClient.getAllActiveSymbols();

        if (symbols.isEmpty()) {
            log.warn("Cron: No active symbols returned from portfolio-service.");
            return;
        }

        log.info("Cron: Refreshing {} symbols", symbols.size());

        for (String symbol : symbols) {

            try {

                PriceResponseDto priceDto =
                        forceRefresh(symbol);

                log.debug(
                        "Cron: Updated price for {}: {}",
                        symbol,
                        priceDto.getCurrentPrice()
                );

            } catch (Exception e) {

                log.error(
                        "Cron: Failed to refresh price for {}: {}",
                        symbol,
                        e.getMessage()
                );
            }
        }

        log.info("Cron: Price refresh complete.");
    }

    // ─────────────────────────────────────────────────────────────────────

    @Transactional
    protected void persistToDb(PriceResponseDto dto) {

        PriceCache entity =
                priceCacheRepository.findBySymbol(dto.getSymbol())
                        .orElse(
                                PriceCache.builder()
                                        .symbol(dto.getSymbol())
                                        .build()
                        );

        entity.setPreviousPrice(
                entity.getCurrentPrice() != null
                        ? entity.getCurrentPrice()
                        : dto.getPreviousPrice()
        );

        entity.setCurrentPrice(dto.getCurrentPrice());
        entity.setChangePercent(dto.getChangePercent());
        entity.setUpdatedAt(LocalDateTime.now());

        priceCacheRepository.save(entity);

        // Store history
        Optional<PriceHistory> latestHistory =
                priceHistoryRepository
                        .findTopBySymbolOrderByRecordedAtDesc(dto.getSymbol());

        boolean shouldInsertHistory =
                latestHistory.isEmpty()
                        || latestHistory.get()
                        .getPrice()
                        .compareTo(dto.getCurrentPrice()) != 0;

        if (shouldInsertHistory) {

            PriceHistory history = PriceHistory.builder()
                    .symbol(dto.getSymbol())
                    .price(dto.getCurrentPrice())
                    .priceDate(LocalDate.now())
                    .build();

            priceHistoryRepository.save(history);

            log.debug(
                    "Inserted new history row for {}",
                    dto.getSymbol()
            );

        } else {

            log.debug(
                    "Skipped duplicate history insert for {}",
                    dto.getSymbol()
            );
        }
    }

    private void publishPriceUpdatedEvent(PriceResponseDto dto) {

        PriceUpdatedEvent event = PriceUpdatedEvent.builder()
                .symbol(dto.getSymbol())
                .currentPrice(dto.getCurrentPrice())
                .previousPrice(dto.getPreviousPrice())
                .changePercent(dto.getChangePercent())
                .updatedAt(LocalDateTime.now())
                .build();

        priceEventPublisher.publishPriceUpdated(event);
    }

    private String normalizeSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol must not be blank");
        }
        return symbol.trim().toUpperCase();
    }

    private PriceResponseDto getCachedPrice(String symbol) {
        Cache cache = cacheManager.getCache(STOCK_PRICES_CACHE_NAME);
        if (cache == null) {
            log.warn("Cache '{}' is not available", STOCK_PRICES_CACHE_NAME);
            return null;
        }

        return cache.get(symbol, PriceResponseDto.class);
    }

    private void putCachedPrice(String symbol, PriceResponseDto priceDto) {
        Cache cache = cacheManager.getCache(STOCK_PRICES_CACHE_NAME);
        if (cache == null) {
            log.warn("Cache '{}' is not available", STOCK_PRICES_CACHE_NAME);
            return;
        }

        cache.put(symbol, withCacheFlag(priceDto, false));
    }

    private PriceResponseDto withCacheFlag(PriceResponseDto dto, boolean fromCache) {
        return PriceResponseDto.builder()
                .symbol(dto.getSymbol())
                .currentPrice(dto.getCurrentPrice())
                .previousPrice(dto.getPreviousPrice())
                .changePercent(dto.getChangePercent())
                .updatedAt(dto.getUpdatedAt())
                .fromCache(fromCache)
                .build();
    }

    private long countCachedSymbols() {

        try {
            Set<String> keys =
                    stringRedisTemplate.keys(STOCK_PRICES_CACHE_KEY_PATTERN);

            return keys == null ? 0 : keys.size();

        } catch (Exception e) {

            log.warn("Could not count cached symbols: {}", e.getMessage());

            return 0;
        }
    }
}
