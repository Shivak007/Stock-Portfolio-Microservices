package com.portfolio.pricefetcher.controller;

import com.portfolio.pricefetcher.dto.response.CacheStatsDto;
import com.portfolio.pricefetcher.dto.response.PriceResponseDto;
import com.portfolio.pricefetcher.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
@Tag(name = "Price Fetcher", description = "Stock price retrieval with Redis cache")
public class PriceController {

    private final PriceService priceService;

    @Operation(summary = "Get current price (Redis cached)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Price returned"),
            @ApiResponse(responseCode = "404", description = "Symbol not found")
    })
    @GetMapping("/{symbol}")
    public ResponseEntity<PriceResponseDto> getCurrentPrice(@PathVariable String symbol) {
        return ResponseEntity.ok(priceService.getPrice(symbol.toUpperCase()));
    }

    @Operation(summary = "Batch fetch multiple symbols")
    @PostMapping("/batch")
    public ResponseEntity<List<PriceResponseDto>> getBatchPrices(@RequestBody List<String> symbols) {
        return ResponseEntity.ok(priceService.getBatchPrices(symbols));
    }

    @Operation(summary = "Force-refresh price for symbol (Admin only)")
    @PostMapping("/refresh")
    public ResponseEntity<PriceResponseDto> forceRefresh(
            @RequestParam String symbol,
            @RequestHeader("X-User-Roles") String roles) {
        // Gateway ensures only ADMIN can reach this — roles header is informational
        return ResponseEntity.ok(priceService.forceRefresh(symbol.toUpperCase()));
    }

    @Operation(summary = "Price history for last N days")
    @GetMapping("/history/{symbol}")
    public ResponseEntity<List<PriceResponseDto>> getPriceHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(priceService.getPriceHistory(symbol.toUpperCase(), days));
    }

    @Operation(summary = "View cache hit/miss stats (Admin only)")
    @GetMapping("/cache/status")
    public ResponseEntity<CacheStatsDto> getCacheStats() {
        return ResponseEntity.ok(priceService.getCacheStats());
    }
}
