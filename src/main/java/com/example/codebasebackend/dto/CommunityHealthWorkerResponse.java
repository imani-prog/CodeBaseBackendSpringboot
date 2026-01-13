package com.example.codebasebackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class CommunityHealthWorkerResponse {
    private Long id;
    private String code;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private String city;
    private String state;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long hospitalId;
    private String status;
    private String specialization;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Assignment & Region
    private String region;
    private Integer assignedPatients;
    private LocalDate startDate;
    private OffsetDateTime lastStatusUpdate;

    // Performance Metrics
    private Integer monthlyVisits;
    private BigDecimal successRate;
    private String responseTime;
    private BigDecimal rating;

    // Computed Fields (not stored in DB)
    private String fullName; // firstName + middleName + lastName
    private String avatar; // Initials (e.g., "GA" for Grace Akinyi)
}


