package com.portfolio.pricefetcher.client;

import com.portfolio.pricefetcher.dto.response.PriceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinnhubClient {

    private final RestTemplate restTemplate;

    @Value("${price.external.api-key}")
    private String apiKey;

    @Value("${price.external.base-url}")
    private String baseUrl;

    /**
     * Fetch real-time stock price from Finnhub.
     */
    public PriceResponseDto fetchPrice(String symbol) {

        String normalizedSymbol = symbol.toUpperCase();

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("symbol", normalizedSymbol)
                .queryParam("token", apiKey)
                .toUriString();

        log.info("Fetching stock price for {}", normalizedSymbol);

        try {

            Map<String, Object> response =
                    restTemplate.getForObject(url, Map.class);

            validateResponse(response, normalizedSymbol);

            BigDecimal currentPrice =
                    toBigDecimal(response.get("c"));

            BigDecimal previousClose =
                    toBigDecimal(response.get("pc"));

            BigDecimal changePercent =
                    toBigDecimal(response.get("dp"));

            LocalDateTime updatedAt =
                    toLocalDateTime(response.get("t"));

            return PriceResponseDto.builder()
                    .symbol(normalizedSymbol)
                    .currentPrice(currentPrice)
                    .previousPrice(previousClose)
                    .changePercent(changePercent)
                    .updatedAt(updatedAt)
                    .fromCache(false)
                    .build();

        } catch (RestClientException e) {

            log.error("Finnhub API error for {} : {}", normalizedSymbol, e.getMessage());

            throw new RuntimeException(
                    "Failed to fetch stock price from Finnhub for symbol: "
                            + normalizedSymbol
            );

        } catch (Exception e) {

            log.error("Unexpected error while fetching {} : {}", normalizedSymbol, e.getMessage());

            throw new RuntimeException(
                    "Unexpected error while fetching stock price for symbol: "
                            + normalizedSymbol
            );
        }
    }

    /**
     * Validate Finnhub response.
     */
    private void validateResponse(Map<String, Object> response, String symbol) {

        if (response == null || !response.containsKey("c")) {

            throw new RuntimeException(
                    "Invalid response received from Finnhub for symbol: "
                            + symbol
            );
        }

        BigDecimal currentPrice =
                toBigDecimal(response.get("c"));

        if (currentPrice.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Finnhub returned invalid stock price for symbol: "
                            + symbol
            );
        }
    }

    /**
     * Safely convert object to BigDecimal.
     */
    private BigDecimal toBigDecimal(Object value) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Convert epoch seconds to LocalDateTime.
     */
    private LocalDateTime toLocalDateTime(Object timestampSeconds) {

        if (timestampSeconds == null) {
            return LocalDateTime.now();
        }

        try {

            long epochSeconds =
                    Long.parseLong(timestampSeconds.toString());

            return LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(epochSeconds),
                    ZoneId.systemDefault()
            );

        } catch (Exception e) {

            return LocalDateTime.now();
        }
    }
}