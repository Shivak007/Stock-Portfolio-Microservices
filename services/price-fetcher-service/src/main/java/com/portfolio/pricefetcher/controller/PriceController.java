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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
@Tag(name = "Price Fetcher", description = "Stock price retrieval with Redis cache")
@Validated
public class PriceController {

    private final PriceService priceService;

    @Operation(summary = "Get current price (Redis cached)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Price returned"),
            @ApiResponse(responseCode = "404", description = "Symbol not found")
    })
    @GetMapping("/{symbol}")
    public ResponseEntity<PriceResponseDto> getCurrentPrice(@PathVariable
                                                                @NotBlank(message = "Symbol cannot be blank")
                                                                @Pattern(
                                                                        regexp = "^[A-Z]{1,5}(?:[./][A-Z]{1,3})?$",
                                                                        message = "Invalid stock symbol"
                                                                )
                                                                String symbol) {
        return ResponseEntity.ok(priceService.getPrice(symbol.toUpperCase()));
    }

    @Operation(summary = "Batch fetch multiple symbols")
    @PostMapping("/batch")
    public ResponseEntity<List<PriceResponseDto>> getBatchPrices(@RequestBody
                                                                     @Valid
                                                                     @Size(min = 1, max = 50, message = "Batch size must be between 1 and 50")
                                                                     List<
                                                                             @NotBlank(message = "Symbol cannot be blank")
                                                                             @Pattern(
                                                                                     regexp = "^[A-Z]{1,5}(?:[./][A-Z]{1,3})?$",
                                                                                     message = "Invalid stock symbol"
                                                                             )
                                                                                     String
                                                                             > symbols) {
        return ResponseEntity.ok(priceService.getBatchPrices(symbols));
    }

    @Operation(summary = "Force-refresh price for symbol")
    @PostMapping("/refresh")
    public ResponseEntity<PriceResponseDto> forceRefresh(
            @RequestParam String symbol,
            @RequestHeader("X-User-Roles") String roles) {
        // Gateway ensures only ADMIN can reach this — roles header is informational
        return ResponseEntity.ok(priceService.forceRefresh(symbol.toUpperCase()));
    }

    @Operation(summary = "Price history for last N Rows")
    @GetMapping("/history/{symbol}")
    public ResponseEntity<List<PriceResponseDto>> getPriceHistory (
            @PathVariable String symbol,
            @RequestParam(defaultValue = "7")
            @Min(value = 1, message = "Days must be at least 1")
            @Max(value = 365, message = "Days cannot exceed 365")
            int days ) {
        return ResponseEntity.ok(priceService.getPriceHistory(symbol.toUpperCase(), days));
    }

    @Operation(summary = "View cache hit/miss stats ")
    @GetMapping("/cache/status")
    public ResponseEntity<CacheStatsDto> getCacheStats() {
        return ResponseEntity.ok(priceService.getCacheStats());
    }
}
