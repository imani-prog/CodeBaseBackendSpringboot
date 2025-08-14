package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationUpdateRequest {
    @NotNull
    private BigDecimal latitude;
    @NotNull
    private BigDecimal longitude;
}

