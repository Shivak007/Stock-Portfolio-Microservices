package com.portfolio.portfolioservice.service.impl;

import com.portfolio.portfolioservice.dto.request.CreatePortfolioRequestDto;
import com.portfolio.portfolioservice.dto.request.HoldingRequestDto;
import com.portfolio.portfolioservice.dto.response.HoldingResponseDto;
import com.portfolio.portfolioservice.dto.response.PortfolioSummaryDto;
import com.portfolio.portfolioservice.entity.Holding;
import com.portfolio.portfolioservice.entity.Portfolio;
import com.portfolio.portfolioservice.exception.custom.ResourceNotFoundException;
import com.portfolio.portfolioservice.feign.PriceFeignClient;
import com.portfolio.portfolioservice.repository.HoldingRepository;
import com.portfolio.portfolioservice.repository.PortfolioRepository;
import com.portfolio.portfolioservice.service.PortfolioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final PriceFeignClient priceFeignClient;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository,
                                HoldingRepository holdingRepository,
                                PriceFeignClient priceFeignClient) {
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
        this.priceFeignClient = priceFeignClient;
    }

    @Override
    public List<Portfolio> getAllPortfolios(Long userId) {
        return portfolioRepository.findByUserIdAndIsActiveTrue(userId);
    }

    @Override
    @Transactional
    public Portfolio createPortfolio(Long userId, CreatePortfolioRequestDto request) {
        Portfolio portfolio = Portfolio.builder()
                .userId(userId)
                .name(request.getName())
                .description(request.getDescription())
                .currency(request.getCurrency())
                .isActive(true)
                .build();
        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio getPortfolioById(Long id, Long userId) {
        return portfolioRepository.findById(id)
                .filter(p -> p.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
    }

    @Override
    @Transactional
    public Portfolio updatePortfolio(Long id, Long userId, CreatePortfolioRequestDto request) {
        Portfolio portfolio = getPortfolioById(id, userId);
        if (request.getName() != null) portfolio.setName(request.getName());
        if (request.getDescription() != null) portfolio.setDescription(request.getDescription());
        return portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
    public void deletePortfolio(Long id, Long userId) {
        Portfolio portfolio = getPortfolioById(id, userId);
        portfolio.setIsActive(false);
        portfolioRepository.save(portfolio);
    }

    @Override
    public PortfolioSummaryDto getPortfolioSummary(Long id, Long userId) {
        Portfolio portfolio = getPortfolioById(id, userId);
        List<Holding> holdings = holdingRepository.findActiveHoldingsByPortfolio(id);

        // Fetch current prices in batch
        List<String> symbols = holdings.stream().map(Holding::getStockSymbol).distinct().collect(Collectors.toList());
        Map<String, BigDecimal> prices = priceFeignClient.getBatchPrices(symbols);

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalCurrent = BigDecimal.ZERO;

        for (Holding h : holdings) {
            BigDecimal invested = h.getBuyPrice().multiply(h.getQuantity());
            BigDecimal currentPrice = prices.getOrDefault(h.getStockSymbol(), h.getBuyPrice());
            BigDecimal current = currentPrice.multiply(h.getQuantity());
            totalInvested = totalInvested.add(invested);
            totalCurrent = totalCurrent.add(current);
        }

        BigDecimal gainLoss = totalCurrent.subtract(totalInvested);
        BigDecimal gainLossPercent = totalInvested.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : gainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        return PortfolioSummaryDto.builder()
                .portfolioId(portfolio.getId())
                .name(portfolio.getName())
                .totalInvestedValue(totalInvested.setScale(2, RoundingMode.HALF_UP))
                .totalCurrentValue(totalCurrent.setScale(2, RoundingMode.HALF_UP))
                .totalGainLoss(gainLoss.setScale(2, RoundingMode.HALF_UP))
                .totalGainLossPercent(gainLossPercent.setScale(2, RoundingMode.HALF_UP))
                .holdingCount(holdings.size())
                .currency(portfolio.getCurrency())
                .asOfTime(LocalDateTime.now())
                .build();
    }

    @Override
    public List<HoldingResponseDto> getHoldings(Long portfolioId, Long userId) {
        getPortfolioById(portfolioId, userId); // ownership check
        List<Holding> holdings = holdingRepository.findActiveHoldingsByPortfolio(portfolioId);
        List<String> symbols = holdings.stream().map(Holding::getStockSymbol).distinct().collect(Collectors.toList());
        Map<String, BigDecimal> prices = priceFeignClient.getBatchPrices(symbols);
        return holdings.stream().map(h -> toHoldingResponseDto(h, prices.getOrDefault(h.getStockSymbol(), h.getBuyPrice()))).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HoldingResponseDto addHolding(Long userId, HoldingRequestDto request) {
        Portfolio portfolio = getPortfolioById(request.getPortfolioId(), userId);
        Holding holding = Holding.builder()
                .portfolio(portfolio)
                .stockSymbol(request.getStockSymbol().toUpperCase())
                .quantity(request.getQuantity())
                .buyPrice(request.getBuyPrice())
                .buyDate(request.getBuyDate())
                .isDeleted(false)
                .build();
        holding = holdingRepository.save(holding);
        return toHoldingResponseDto(holding, request.getBuyPrice());
    }

    @Override
    public HoldingResponseDto getHolding(Long id) {
        Holding holding = holdingRepository.findById(id)
                .filter(h -> !h.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found with id: " + id));
        Map<String, Object> priceData = priceFeignClient.getCurrentPrice(holding.getStockSymbol());
        BigDecimal price = priceData.containsKey("price")
                ? new BigDecimal(priceData.get("price").toString())
                : holding.getBuyPrice();
        return toHoldingResponseDto(holding, price);
    }

    @Override
    @Transactional
    public HoldingResponseDto updateHolding(Long id, HoldingRequestDto request) {
        Holding holding = holdingRepository.findById(id)
                .filter(h -> !h.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found with id: " + id));
        if (request.getQuantity() != null) holding.setQuantity(request.getQuantity());
        if (request.getBuyPrice() != null) holding.setBuyPrice(request.getBuyPrice());
        if (request.getBuyDate() != null) holding.setBuyDate(request.getBuyDate());
        return toHoldingResponseDto(holdingRepository.save(holding), holding.getBuyPrice());
    }

    @Override
    @Transactional
    public void deleteHolding(Long id) {
        Holding holding = holdingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found with id: " + id));
        holding.setIsDeleted(true);
        holdingRepository.save(holding);
    }

    @Override
    public HoldingResponseDto getHoldingGainLoss(Long id) {
        return getHolding(id);
    }

    @Override
    public List<String> getAllActiveSymbols() {
        return holdingRepository.findAllActiveSymbols();
    }

    private HoldingResponseDto toHoldingResponseDto(Holding h, BigDecimal currentPrice) {
        BigDecimal currentValue = currentPrice.multiply(h.getQuantity());
        BigDecimal investedValue = h.getBuyPrice().multiply(h.getQuantity());
        BigDecimal gainLoss = currentValue.subtract(investedValue);
        BigDecimal gainLossPercent = investedValue.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : gainLoss.divide(investedValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        return HoldingResponseDto.builder()
                .id(h.getId())
                .stockSymbol(h.getStockSymbol())
                .quantity(h.getQuantity())
                .buyPrice(h.getBuyPrice())
                .buyDate(h.getBuyDate())
                .currentPrice(currentPrice.setScale(2, RoundingMode.HALF_UP))
                .currentValue(currentValue.setScale(2, RoundingMode.HALF_UP))
                .gainLoss(gainLoss.setScale(2, RoundingMode.HALF_UP))
                .gainLossPercent(gainLossPercent.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}
