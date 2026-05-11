package com.portfolio.reportservice.controller;

import com.portfolio.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Report Service", description = "APIs for generating portfolio reports")
public class ReportController {

    private final ReportService reportService;
    @Operation(
            summary = "Download Portfolio Report",
            description = "Generates PDF or Excel portfolio report for a user"
    )
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadReport(
            @RequestParam String type,
            @RequestParam Long userId
    ) {

        ByteArrayInputStream report = reportService.generateReport(type, userId);

        String timestamp =
                java.time.LocalDateTime.now()
                        .toString()
                        .replace(":", "-");

        String fileName =
                type.equalsIgnoreCase("PDF")
                        ? "portfolio-report-" + timestamp + ".pdf"
                        : "portfolio-report-" + timestamp + ".xlsx";

        MediaType mediaType =
                type.equalsIgnoreCase("PDF")
                        ? MediaType.APPLICATION_PDF
                        : MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName
                )
                .contentType(mediaType)
                .body(new InputStreamResource(report));
    }
}