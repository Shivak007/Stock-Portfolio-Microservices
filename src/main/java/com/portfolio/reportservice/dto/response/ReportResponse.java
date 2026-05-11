package com.portfolio.reportservice.dto.response;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportResponse {

    private Long id;

    private Long userId;

    private String reportType;

    private String fileName;

    private LocalDateTime generatedAt;
}