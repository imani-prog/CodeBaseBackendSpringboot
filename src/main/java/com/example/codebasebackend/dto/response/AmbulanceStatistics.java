package com.example.codebasebackend.dto.response;
import lombok.*;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmbulanceStatistics {
    private Long totalFleet;
    private Long available;
    private Long busy;
    private Long maintenance;
    private Long outOfService;
    private Double averageMileage;
    private Double averageFuelLevel;
    private Long totalDispatches;
    private Double averageResponseTime;
    private Long basicLifeSupport;
    private Long advancedLifeSupport;
    private Long criticalCare;
    private Long patientTransport;
}
