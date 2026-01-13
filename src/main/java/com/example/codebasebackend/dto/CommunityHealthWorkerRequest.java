package com.example.codebasebackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CommunityHealthWorkerRequest {
    @NotBlank
    private String firstName;
    private String middleName;
    @NotBlank
    private String lastName;
    @Email
    private String email;
    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long hospitalId;
    private String specialization;

    // Regional & Workload
    private String region;

    @Min(value = 0, message = "Assigned patients cannot be negative")
    private Integer assignedPatients;

    private LocalDate startDate;

    // Performance Metrics (optional for creation, can be updated later)
    @Min(value = 0, message = "Monthly visits cannot be negative")
    private Integer monthlyVisits;

    @DecimalMin(value = "0.00", message = "Success rate must be between 0 and 100")
    @DecimalMax(value = "100.00", message = "Success rate must be between 0 and 100")
    private BigDecimal successRate;

    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?hrs$", message = "Response time format: e.g., '1.8hrs'")
    private String responseTime;

    @DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    private BigDecimal rating;

    private String status; // AVAILABLE, BUSY, OFFLINE
}

