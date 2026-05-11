package com.portfolio.reportservice.dto.request;
import lombok.Data;

@Data
public class ReportRequest {

    private Long userId;

    private String reportType;
}