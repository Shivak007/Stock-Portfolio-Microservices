package com.portfolio.portfolioservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePortfolioRequestDto {

    @NotBlank(message = "Portfolio name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    private String description;

    @NotBlank(message = "Currency is required")
    private String currency;
}
