package com.portfolio.alertservice.service;

import com.portfolio.alertservice.dto.request.CreateAlertRequestDto;
import com.portfolio.alertservice.entity.Alert;
import com.portfolio.alertservice.enums.AlertStatus;

import java.util.List;

public interface AlertService {
    Alert createAlert(Long userId, CreateAlertRequestDto request);
    List<Alert> getAlerts(Long userId);
    Alert getAlert(Long id, Long userId);
    Alert updateAlert(Long id, Long userId, CreateAlertRequestDto request);
    Alert updateAlertStatus(Long id, Long userId, AlertStatus status);
    void deleteAlert(Long id, Long userId);
    List<Alert> getTriggeredAlerts(Long userId);
}
