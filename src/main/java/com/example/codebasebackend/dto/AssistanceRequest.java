package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssistanceRequest {
    private Long patientId; // optional
    private String callerName;
    private String callerPhone;
    private String incidentType;
    private String notes;

    @NotNull
    private BigDecimal pickupLatitude;
    @NotNull
    private BigDecimal pickupLongitude;

    private String pickupAddressLine1;
    private String pickupAddressLine2;
    private String pickupCity;
    private String pickupState;
    private String pickupPostalCode;
    private String pickupCountry;

    private Long hospitalId; // optional preferred hospital

    // LOW, MEDIUM, HIGH, CRITICAL
    private String priority;

    private String dropoffAddressLine1;
    private String dropoffAddressLine2;
    private String dropoffCity;
    private String dropoffState;
    private String dropoffPostalCode;
    private String dropoffCountry;
    private BigDecimal dropoffLatitude;
    private BigDecimal dropoffLongitude;
}
