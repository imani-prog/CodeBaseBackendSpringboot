package com.example.codebasebackend.dto;

import com.example.codebasebackend.Entities.UserRole;
import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.Patient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 80)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String fullName;
    private String phone;
    private UserRole role;

    @Valid
    private PatientProfileRequest patient;

    @Valid
    private ChwProfileRequest communityHealthWorker;

    @Data
    public static class PatientProfileRequest {
        private String firstName;
        private String middleName;
        private String lastName;
        private Patient.Gender gender;
        private LocalDate dateOfBirth;
        private String email;
        private String phone;
        private String secondaryPhone;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String nationalId;
        private String insuranceMemberId;
        private String insuranceProviderName;
        private String emergencyContactName;
        private String emergencyContactRelation;
        private String emergencyContactPhone;
        private String allergies;
        private String medications;
        private String chronicConditions;
        private Patient.BloodType bloodType;
        private String preferredLanguage;
        private Patient.PatientStatus status;
        private Patient.MaritalStatus maritalStatus;
        private Boolean consentToShareData;
        private Boolean smsOptIn;
        private Boolean emailOptIn;
        private String notes;
        private Long hospitalId;
    }

    @Data
    public static class ChwProfileRequest {
        private String code;
        private String firstName;
        private String middleName;
        private String lastName;
        private String email;
        private String phone;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String region;
        private Integer assignedPatients;
        private LocalDate startDate;
        private String responseTime;
        private String specialization;
        private CommunityHealthWorkers.Status status;
        private BigDecimal successRate;
        private BigDecimal rating;
        private Integer monthlyVisits;
        private Long hospitalId;
    }
}