package com.portfolio.alertservice.service.impl;

import com.portfolio.alertservice.dto.request.CreateAlertRequestDto;
import com.portfolio.alertservice.entity.Alert;
import com.portfolio.alertservice.enums.AlertCondition;
import com.portfolio.alertservice.enums.AlertStatus;
import com.portfolio.alertservice.exception.custom.ResourceNotFoundException;
import com.portfolio.alertservice.repository.AlertRepository;
import com.portfolio.alertservice.service.AlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    public AlertServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    @Transactional
    public Alert createAlert(Long userId, CreateAlertRequestDto request) {
        Alert alert = Alert.builder()
                .userId(userId)
                .alertType(request.getAlertType())
                .stockSymbol(request.getStockSymbol() != null ? request.getStockSymbol().toUpperCase() : null)
                .targetPrice(request.getTargetPrice())
                .portfolioId(request.getPortfolioId())
                .lossThresholdPercent(request.getLossThresholdPercent())
                .condition(request.getCondition() != null ? request.getCondition() : AlertCondition.ABOVE)
                .status(AlertStatus.ACTIVE)
                .build();
        return alertRepository.save(alert);
    }

    @Override
    public List<Alert> getAlerts(Long userId) {
        return alertRepository.findByUserId(userId);
    }

    @Override
    public Alert getAlert(Long id, Long userId) {
        return alertRepository.findById(id)
                .filter(a -> a.getUserId().equals(userId) && a.getStatus() != AlertStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
    }

    @Override
    @Transactional
    public Alert updateAlert(Long id, Long userId, CreateAlertRequestDto request) {
        Alert alert = getAlert(id, userId);
        if (request.getTargetPrice() != null) alert.setTargetPrice(request.getTargetPrice());
        if (request.getCondition() != null) alert.setCondition(request.getCondition());
        if (request.getLossThresholdPercent() != null) alert.setLossThresholdPercent(request.getLossThresholdPercent());
        return alertRepository.save(alert);
    }

    @Override
    @Transactional
    public Alert updateAlertStatus(Long id, Long userId, AlertStatus status) {
        Alert alert = getAlert(id, userId);
        alert.setStatus(status);
        return alertRepository.save(alert);
    }

    @Override
    @Transactional
    public void deleteAlert(Long id, Long userId) {
        Alert alert = getAlert(id, userId);
        alert.setStatus(AlertStatus.DELETED);
        alertRepository.save(alert);
    }

    @Override
    public List<Alert> getTriggeredAlerts(Long userId) {
        return alertRepository.findByUserIdAndStatus(userId, AlertStatus.TRIGGERED);
    }
}
