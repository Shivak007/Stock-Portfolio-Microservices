package com.portfolio.reportservice.factory;

import com.portfolio.reportservice.exception.ReportGenerationException;
import com.portfolio.reportservice.generator.ReportGenerator;
import com.portfolio.reportservice.generator.impl.ExcelReportGenerator;
import com.portfolio.reportservice.generator.impl.PdfReportGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportFactory {

    private final PdfReportGenerator pdfReportGenerator;

    private final ExcelReportGenerator excelReportGenerator;

    public ReportGenerator getReportGenerator(String type) {

        if ("PDF".equalsIgnoreCase(type)) {
            return pdfReportGenerator;
        }

        if ("EXCEL".equalsIgnoreCase(type)) {
            return excelReportGenerator;
        }

        throw new ReportGenerationException(
                "Invalid report type: " + type
        );    }
}