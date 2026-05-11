package com.portfolio.reportservice.generator.impl;

import com.portfolio.reportservice.dto.response.PortfolioItemResponse;
import com.portfolio.reportservice.dto.response.PortfolioResponse;
import com.portfolio.reportservice.generator.ReportGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
@Component
public class ExcelReportGenerator implements ReportGenerator {

    @Override
    public ByteArrayInputStream generateReport(
            PortfolioResponse portfolio
    ) {

        try {

            log.info(
                    "Generating Excel report for {} stocks",
                    portfolio.getStocks().size()
            );

            XSSFWorkbook workbook = new XSSFWorkbook();

            CellStyle headerStyle = workbook.createCellStyle();

            Font headerFont = workbook.createFont();

            headerFont.setBold(true);

            headerFont.setColor(
                    IndexedColors.WHITE.getIndex()
            );

            headerStyle.setFont(headerFont);

            headerStyle.setFillForegroundColor(
                    IndexedColors.DARK_BLUE.getIndex()
            );

            headerStyle.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND
            );

            headerStyle.setAlignment(
                    HorizontalAlignment.CENTER
            );

            XSSFSheet sheet =
                    workbook.createSheet(
                            "Portfolio Report"
                    );

            Row header = sheet.createRow(0);

            String[] columns = {
                    "Stock",
                    "Quantity",
                    "Buy Price",
                    "Current Price",
                    "Profit"
            };

            for (int i = 0; i < columns.length; i++) {

                Cell cell = header.createCell(i);

                cell.setCellValue(columns[i]);

                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;

            for (PortfolioItemResponse item : portfolio.getStocks()) {

                log.debug(
                        "Processing stock: {}",
                        item.getStockSymbol()
                );

                Row row = sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(
                                item.getStockSymbol() != null
                                        ? item.getStockSymbol()
                                        : "N/A"
                        );

                row.createCell(1)
                        .setCellValue(
                                item.getQuantity() != null
                                        ? item.getQuantity().doubleValue()
                                        : 0.0
                        );

                row.createCell(2)
                        .setCellValue(
                                item.getBuyPrice() != null
                                        ? item.getBuyPrice().doubleValue()
                                        : 0.0
                        );

                row.createCell(3)
                        .setCellValue(
                                item.getCurrentPrice() != null
                                        ? item.getCurrentPrice().doubleValue()
                                        : 0.0
                        );

                row.createCell(4)
                        .setCellValue(
                                item.getGainLoss() != null
                                        ? item.getGainLoss().doubleValue()
                                        : 0.0
                        );
            }

            for (int i = 0; i < 5; i++) {

                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            workbook.write(out);

            workbook.close();

            log.info(
                    "Excel report generated successfully"
            );

            return new ByteArrayInputStream(
                    out.toByteArray()
            );

        } catch (Exception e) {

            log.error(
                    "Error while generating Excel report",
                    e
            );

            throw new RuntimeException(
                    "Failed to generate Excel report"
            );
        }
    }
}