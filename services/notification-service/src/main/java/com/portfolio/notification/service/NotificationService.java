package com.portfolio.notification.service;

import com.portfolio.notification.dto.NotificationLogDto;
import com.portfolio.notification.entity.NotificationLog;
import com.portfolio.notification.enums.NotificationStatus;
import com.portfolio.notification.enums.NotificationType;
import com.portfolio.notification.exception.ResourceNotFoundException;
import com.portfolio.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationLogRepository repository;
    private final EmailService emailService;

    @Transactional
    public NotificationLog createAndSend(Long userId, String email, String subject,
                                          String body, NotificationType type) {
        NotificationLog log = NotificationLog.builder()
                .userId(userId)
                .recipientEmail(email)
                .subject(subject)
                .body(body)
                .type(type)
                .status(NotificationStatus.PENDING)
                .retryCount(0)
                .isRead(false)
                .build();

        NotificationLog saved = repository.save(log);

        try {
            emailService.sendEmail(email, subject, body);
            saved.setStatus(NotificationStatus.SENT);
            saved.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            saved.setStatus(NotificationStatus.FAILED);
            saved.setErrorMessage(e.getMessage());
            saved.setRetryCount(saved.getRetryCount() + 1);
            throw e; // re-throw for RabbitMQ retry
        } finally {
            repository.save(saved);
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public Page<NotificationLogDto> getNotificationsForUser(Long userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public NotificationLogDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public void markAsRead(Long id) {
        NotificationLog n = findOrThrow(id);
        n.setRead(true);
        repository.save(n);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return repository.countByUserIdAndIsReadFalseAndStatus(userId, NotificationStatus.SENT);
    }

    private NotificationLog findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
    }

    private NotificationLogDto toDto(NotificationLog n) {
        return NotificationLogDto.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .recipientEmail(n.getRecipientEmail())
                .subject(n.getSubject())
                .type(n.getType())
                .status(n.getStatus())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .sentAt(n.getSentAt())
                .build();
    }
}
