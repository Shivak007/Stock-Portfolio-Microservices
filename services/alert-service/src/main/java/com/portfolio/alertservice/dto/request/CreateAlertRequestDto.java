package com.portfolio.alertservice.dto.request;

import com.portfolio.alertservice.enums.AlertCondition;
import com.portfolio.alertservice.enums.AlertType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAlertRequestDto {

    @NotNull(message = "Alert type is required")
    private AlertType alertType;

    private String stockSymbol;
    private BigDecimal targetPrice;
    private Long portfolioId;
    private BigDecimal lossThresholdPercent;
    private AlertCondition condition;
}
