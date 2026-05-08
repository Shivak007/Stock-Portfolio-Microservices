package com.portfolio.portfolioservice.service;

import com.portfolio.portfolioservice.dto.request.CreatePortfolioRequestDto;
import com.portfolio.portfolioservice.dto.request.HoldingRequestDto;
import com.portfolio.portfolioservice.dto.response.HoldingResponseDto;
import com.portfolio.portfolioservice.dto.response.PortfolioSummaryDto;
import com.portfolio.portfolioservice.entity.Portfolio;

import java.util.List;

public interface PortfolioService {
    List<Portfolio> getAllPortfolios(Long userId);
    Portfolio createPortfolio(Long userId, CreatePortfolioRequestDto request);
    Portfolio getPortfolioById(Long id, Long userId);
    Portfolio updatePortfolio(Long id, Long userId, CreatePortfolioRequestDto request);
    void deletePortfolio(Long id, Long userId);
    PortfolioSummaryDto getPortfolioSummary(Long id, Long userId);
    List<HoldingResponseDto> getHoldings(Long portfolioId, Long userId);
    HoldingResponseDto addHolding(Long userId, HoldingRequestDto request);
    HoldingResponseDto getHolding(Long id);
    HoldingResponseDto updateHolding(Long id, HoldingRequestDto request);
    void deleteHolding(Long id);
    HoldingResponseDto getHoldingGainLoss(Long id);
    List<String> getAllActiveSymbols();
}
