package com.example.codebasebackend.dto.response;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Value
@Builder
public class AmbulanceResponse {
    Long id;
    String vehiclePlate;
    String driverName;
    String driverPhone;
    String status;
    String medicName;
    String notes;

    String registrationNumber;
    String model;
    int year;
    String fuelType;
    int capacity;
    boolean equippedForICU;
    boolean gpsEnabled;
    String insurancePolicyNumber;
    String insuranceProvider;

    String type;
    String currentLocation;
    BigDecimal currentLatitude;
    BigDecimal currentLongitude;

    LocalDate lastMaintenanceDate;
    LocalDate nextMaintenanceDate;
    Integer lastMaintenanceMileage;
    Integer mileage;
    Integer fuelLevel;
    OffsetDateTime lastDispatchTime;
    Integer totalDispatches;
    Integer averageResponseMinutes;

    List<String> equipmentList;
    String imageUrl;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    DriverSummary currentDriver;

    @Value
    @Builder
    public static class DriverSummary {
        Long id;
        String name;
        String status;
        String phone;
    }
}

