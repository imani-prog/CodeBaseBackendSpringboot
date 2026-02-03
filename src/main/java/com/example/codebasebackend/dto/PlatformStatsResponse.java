package com.example.codebasebackend.dto;

import com.example.codebasebackend.Entities.PlatformType;
import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformStatsResponse {

    private Map<PlatformType, PlatformUsage> usageByPlatform;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlatformUsage {
        private Integer sessions;
        private Double percentage;
        private Integer avgDuration; // minutes
    }
}
