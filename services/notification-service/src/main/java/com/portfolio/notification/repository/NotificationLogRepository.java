package com.portfolio.notification.repository;

import com.portfolio.notification.entity.NotificationLog;
import com.portfolio.notification.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    Page<NotificationLog> findByUserId(Long userId, Pageable pageable);

    long countByUserIdAndIsReadFalseAndStatus(Long userId, NotificationStatus status);
}
