package com.example.codebasebackend.dto.response;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.dto.DriverSummary;
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
    String status;
    String medicName;
    String notes;

    String registrationNumber;
    String model;
    Integer year;
    String fuelType;
    Integer capacity;

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
    Double averageResponseMinutes;

    List<String> equipmentList;

    String imageUrl;

    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    DriverSummary currentDriver;

    public static AmbulanceResponse from(Ambulances a) {
        return AmbulanceResponse.builder()
                .id(a.getId())
                .vehiclePlate(a.getVehiclePlate())
                .status(a.getStatus() != null ? a.getStatus().name() : null)
                .medicName(a.getMedicName())
                .notes(a.getNotes())
                .registrationNumber(a.getRegistrationNumber())
                .model(a.getModel())
                .year(a.getYear())
                .fuelType(a.getFuelType() != null ? a.getFuelType().name() : null)
                .capacity(a.getCapacity())
                .equippedForICU(a.isEquippedForICU())
                .gpsEnabled(a.isGpsEnabled())
                .insurancePolicyNumber(a.getInsurancePolicyNumber())
                .insuranceProvider(a.getInsuranceProvider())
                .type(a.getType() != null ? a.getType().name() : null)
                .currentLocation(a.getCurrentLocation())
                .currentLatitude(a.getCurrentLatitude())
                .currentLongitude(a.getCurrentLongitude())
                .lastMaintenanceDate(a.getLastMaintenanceDate())
                .nextMaintenanceDate(a.getNextMaintenanceDate())
                .lastMaintenanceMileage(a.getLastMaintenanceMileage())
                .mileage(a.getMileage())
                .fuelLevel(a.getFuelLevel())
                .lastDispatchTime(a.getLastDispatchTime())
                .totalDispatches(a.getTotalDispatches())
                .averageResponseMinutes(a.getAverageResponseMinutes() != null ? Double.valueOf(a.getAverageResponseMinutes()) : null)
                .equipmentList(a.getEquipmentList())
                .imageUrl(a.getImageUrl())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .currentDriver(
                        a.getCurrentDriver() != null
                                ? DriverSummary.builder()
                                .id(a.getCurrentDriver().getId())
                                .name(a.getCurrentDriver().getName())
                                .status(
                                        a.getCurrentDriver().getStatus() != null
                                        ? a.getCurrentDriver().getStatus().name()
                                        : null
                                )
                                .phone(a.getCurrentDriver().getPhone())
                                .build()
                                : null
                )
                .build();
    }
}