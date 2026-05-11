package com.portfolio.reportservice.service;

import java.io.ByteArrayInputStream;

public interface ReportService {

    ByteArrayInputStream generateReport(
            String type,
            Long userId
    );
}