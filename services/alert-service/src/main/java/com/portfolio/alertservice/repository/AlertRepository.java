package com.portfolio.alertservice.repository;

import com.portfolio.alertservice.entity.Alert;
import com.portfolio.alertservice.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUserId(Long userId);
    List<Alert> findByUserIdAndStatus(Long userId, AlertStatus status);
    List<Alert> findByStockSymbolAndStatus(String symbol, AlertStatus status);
}
