package com.example.codebasebackend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PerformanceMetricsRequest {

    @Min(value = 0, message = "Monthly visits cannot be negative")
    private Integer monthlyVisits;

    @DecimalMin(value = "0.00", message = "Success rate must be between 0 and 100")
    @DecimalMax(value = "100.00", message = "Success rate must be between 0 and 100")
    private BigDecimal successRate;

    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?hrs$", message = "Response time format: e.g., '1.8hrs'")
    private String responseTime;

    @DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    private BigDecimal rating;
}

