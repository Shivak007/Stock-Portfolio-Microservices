package com.portfolio.portfolioservice.controller;

import com.portfolio.portfolioservice.dto.request.CreatePortfolioRequestDto;
import com.portfolio.portfolioservice.dto.request.HoldingRequestDto;
import com.portfolio.portfolioservice.dto.response.HoldingResponseDto;
import com.portfolio.portfolioservice.dto.response.PortfolioSummaryDto;
import com.portfolio.portfolioservice.entity.Portfolio;
import com.portfolio.portfolioservice.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Portfolios & Holdings", description = "Manage portfolios and stock holdings")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // ─── Portfolio Endpoints ───────────────────────────────────────────────────

    @GetMapping("/api/portfolios")
    @Operation(summary = "List all portfolios for current user")
    public ResponseEntity<List<Portfolio>> getAllPortfolios(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(portfolioService.getAllPortfolios(userId));
    }

    @PostMapping("/api/portfolios")
    @Operation(summary = "Create new portfolio")
    public ResponseEntity<Portfolio> createPortfolio(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreatePortfolioRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.createPortfolio(userId, request));
    }

    @GetMapping("/api/portfolios/{id}")
    @Operation(summary = "Get portfolio by ID")
    public ResponseEntity<Portfolio> getPortfolio(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(portfolioService.getPortfolioById(id, userId));
    }

    @PutMapping("/api/portfolios/{id}")
    @Operation(summary = "Update portfolio name/description")
    public ResponseEntity<Portfolio> updatePortfolio(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreatePortfolioRequestDto request) {
        return ResponseEntity.ok(portfolioService.updatePortfolio(id, userId, request));
    }

    @DeleteMapping("/api/portfolios/{id}")
    @Operation(summary = "Delete (deactivate) portfolio")
    public ResponseEntity<Void> deletePortfolio(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        portfolioService.deletePortfolio(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/portfolios/{id}/summary")
    @Operation(summary = "Portfolio summary with gain/loss")
    public ResponseEntity<PortfolioSummaryDto> getPortfolioSummary(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(portfolioService.getPortfolioSummary(id, userId));
    }

    @GetMapping("/api/portfolios/{id}/holdings")
    @Operation(summary = "All holdings in a portfolio")
    public ResponseEntity<List<HoldingResponseDto>> getHoldings(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(portfolioService.getHoldings(id, userId));
    }

    // ─── Holding Endpoints ─────────────────────────────────────────────────────

    @PostMapping("/api/holdings")
    @Operation(summary = "Add holding to portfolio")
    public ResponseEntity<HoldingResponseDto> addHolding(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody HoldingRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.addHolding(userId, request));
    }

    @GetMapping("/api/holdings/{id}")
    @Operation(summary = "Get specific holding")
    public ResponseEntity<HoldingResponseDto> getHolding(@PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.getHolding(id));
    }

    @PutMapping("/api/holdings/{id}")
    @Operation(summary = "Edit holding quantity/price")
    public ResponseEntity<HoldingResponseDto> updateHolding(
            @PathVariable Long id,
            @RequestBody HoldingRequestDto request) {
        return ResponseEntity.ok(portfolioService.updateHolding(id, request));
    }

    @DeleteMapping("/api/holdings/{id}")
    @Operation(summary = "Remove holding")
    public ResponseEntity<Void> deleteHolding(@PathVariable Long id) {
        portfolioService.deleteHolding(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/holdings/{id}/gainloss")
    @Operation(summary = "Gain/loss for one holding")
    public ResponseEntity<HoldingResponseDto> getHoldingGainLoss(@PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.getHoldingGainLoss(id));
    }

    @GetMapping("/api/holdings/symbols")
    @Operation(summary = "Get all active stock symbols (used by price-fetcher cron)")
    public ResponseEntity<List<String>> getAllActiveSymbols() {
        return ResponseEntity.ok(portfolioService.getAllActiveSymbols());
    }
}
