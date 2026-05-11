package com.portfolio.reportservice.repository;

import com.portfolio.reportservice.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
}