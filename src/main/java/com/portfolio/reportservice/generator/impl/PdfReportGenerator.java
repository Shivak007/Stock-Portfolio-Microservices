package com.portfolio.reportservice.generator.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfWriter;
import com.portfolio.reportservice.dto.response.PortfolioItemResponse;
import com.portfolio.reportservice.dto.response.PortfolioResponse;
import com.portfolio.reportservice.generator.ReportGenerator;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Slf4j
@Component
public class PdfReportGenerator implements ReportGenerator {

    @Override
    public ByteArrayInputStream generateReport(
            PortfolioResponse portfolio
    ) {

        try {

            Document document = new Document();

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

            Paragraph title =
                    new Paragraph("Stock Portfolio Report", titleFont);

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            document.add(
                    new Paragraph("Generated At: " + LocalDateTime.now())
            );

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);

            table.setWidthPercentage(100);

            addHeader(table, "Stock");
            addHeader(table, "Quantity");
            addHeader(table, "Buy Price");
            addHeader(table, "Current Price");
            addHeader(table, "Profit");
            log.info("Generating PDF report for {} stocks",
                    portfolio.getStocks().size());
            for (PortfolioItemResponse item : portfolio.getStocks()) {

                table.addCell(
                        item.getStockSymbol() != null
                                ? item.getStockSymbol()
                                : "N/A"
                );

                table.addCell(
                        String.format(
                                "%.0f",
                                item.getQuantity()
                        )
                );

                table.addCell(
                        String.format(
                                "%.2f",
                                item.getBuyPrice()
                        )
                );

                table.addCell(
                        String.format(
                                "%.2f",
                                item.getCurrentPrice()
                        )
                );

                table.addCell(
                        String.format(
                                "%.2f",
                                item.getGainLoss()
                        )
                );

                log.debug("Processing stock: {}", item);
            }

            document.add(table);

            document.add(new Paragraph(" "));

            document.add(new Paragraph(" "));

            document.add(
                    new Paragraph(
                            String.format(
                                    "Total Investment: $%.2f",
                                    portfolio.getTotalInvestment()
                            )
                    )
            );

            document.add(
                    new Paragraph(
                            String.format(
                                    "Current Value: $%.2f",
                                    portfolio.getCurrentValue()
                            )
                    )
            );

            document.add(
                    new Paragraph(
                            String.format(
                                    "Total Profit: $%.2f",
                                    portfolio.getTotalProfit()
                            )
                    )
            );
            document.close();
            log.info("PDF report generated successfully");
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {

            throw new RuntimeException("Failed to generate PDF report");
        }
    }

    private void addHeader(
            PdfPTable table,
            String title
    ) {

        PdfPCell header = new PdfPCell();

        Font font = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                12,
                BaseColor.WHITE
        );

        header.setBackgroundColor(BaseColor.DARK_GRAY);

        header.setPadding(5);

        header.setPhrase(new Phrase(title, font));

        table.addCell(header);


    }
}