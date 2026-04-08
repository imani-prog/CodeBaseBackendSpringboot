package com.example.codebasebackend.dto.response;

import com.example.codebasebackend.Entities.AmbulanceDriver;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Value
@Builder
public class AmbulanceDriverResponse {
    Long id;
    String name;
    String licenseNumber;
    String phone;
    String email;
    AmbulanceDriver.DriverStatus status;
    Integer yearsOfExperience;
    String shiftStart;
    String shiftEnd;
    Integer totalTrips;
    Double rating;
    String emergencyContact;
    String avatarUrl;
    LocalDate dateOfBirth;
    LocalDate hireDate;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    AmbulanceSummary currentAmbulance;

    @Value
    @Builder
    public static class AmbulanceSummary {
        Long id;
        String vehiclePlate;
        String status;
        String currentLocation;
    }
}

