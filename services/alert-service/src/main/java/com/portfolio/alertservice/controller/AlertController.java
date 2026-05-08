package com.portfolio.alertservice.controller;

import com.portfolio.alertservice.dto.request.CreateAlertRequestDto;
import com.portfolio.alertservice.entity.Alert;
import com.portfolio.alertservice.enums.AlertStatus;
import com.portfolio.alertservice.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Alerts", description = "Manage price and portfolio alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    @Operation(summary = "Create new alert")
    public ResponseEntity<Alert> createAlert(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateAlertRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createAlert(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get all alerts for current user")
    public ResponseEntity<List<Alert>> getAlerts(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(alertService.getAlerts(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific alert")
    public ResponseEntity<Alert> getAlert(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(alertService.getAlert(id, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update alert config")
    public ResponseEntity<Alert> updateAlert(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateAlertRequestDto request) {
        return ResponseEntity.ok(alertService.updateAlert(id, userId, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Pause / reactivate alert")
    public ResponseEntity<Alert> updateStatus(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam AlertStatus status) {
        return ResponseEntity.ok(alertService.updateAlertStatus(id, userId, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete alert (soft delete)")
    public ResponseEntity<Void> deleteAlert(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        alertService.deleteAlert(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/triggered")
    @Operation(summary = "Get alert history (triggered alerts)")
    public ResponseEntity<List<Alert>> getTriggeredAlerts(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(alertService.getTriggeredAlerts(userId));
    }
}
