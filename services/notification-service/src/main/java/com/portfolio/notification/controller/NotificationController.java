package com.portfolio.notification.controller;

import com.portfolio.notification.dto.NotificationLogDto;
import com.portfolio.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification history and management")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * In a real setup, userId comes from X-User-Id header set by API Gateway after JWT validation.
     * For standalone testing, pass it as a request header directly.
     */
    @Operation(summary = "Get notification history (paginated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Page<NotificationLogDto>> getNotifications(
            @RequestHeader("X-User-Id") Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId, pageable));
    }

    @Operation(summary = "Get specific notification")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationLogDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @Operation(summary = "Mark notification as read")
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Count unread notifications")
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.getUnreadCount(userId)));
    }

    @Operation(summary = "Requeue from DLQ (ADMIN)")
    @PostMapping("/admin/requeue")
    public ResponseEntity<Map<String, String>> requeueDlq() {
        // Placeholder — implement DLQ requeue logic via RabbitAdmin if needed
        return ResponseEntity.ok(Map.of("message", "DLQ requeue triggered"));
    }
}
