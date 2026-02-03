package com.example.codebasebackend.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformOverviewResponse {

    private Integer totalSessions;
    private Integer activeSessions;
    private Integer totalDoctors;
    private Integer onlineDoctors;

    private BigDecimal totalRevenue;
    private Double monthlyGrowth; // percentage
    private Integer avgSessionDuration; // minutes
    private Double patientSatisfaction; // average rating
}
