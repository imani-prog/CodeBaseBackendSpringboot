package com.example.codebasebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformUsageItemResponse {
    private String platform;
    private Integer sessions;
    private Double percentage;
    private Integer avgDurationMinutes;
}

