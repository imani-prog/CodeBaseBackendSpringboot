package com.example.codebasebackend.dto;

import com.example.codebasebackend.Entities.Patient.BloodType;
import com.example.codebasebackend.Entities.Patient.Gender;
import com.example.codebasebackend.Entities.Patient.MaritalStatus;
import com.example.codebasebackend.Entities.Patient.PatientStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class PatientResponse {

    private Long id;

    // Identity
    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;

    // Contact
    private String email;
    private String phone;
    private String secondaryPhone;

    // Address
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;

    // Government/insurance
    private String nationalId;
    private String insuranceMemberId;
    private String insuranceProviderName;

    // Emergency contact
    private String emergencyContactName;
    private String emergencyContactRelation;
    private String emergencyContactPhone;

    // Clinical profile
    private String allergies;
    private String medications;
    private String chronicConditions;
    private BloodType bloodType;

    // Preferences and status
    private String preferredLanguage;
    private PatientStatus status;
    private MaritalStatus maritalStatus;
    private Boolean consentToShareData;
    private Boolean smsOptIn;
    private Boolean emailOptIn;
    private String notes;

    // Audit
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Flattened relationships (no lazy loading issues)
    private Long userId;
    private String userUsername;
    private String userEmail;

    private Long hospitalId;
    private String hospitalName;

    // Static mapper
    public static PatientResponse from(com.example.codebasebackend.Entities.Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .middleName(patient.getMiddleName())
                .lastName(patient.getLastName())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .secondaryPhone(patient.getSecondaryPhone())
                .addressLine1(patient.getAddressLine1())
                .addressLine2(patient.getAddressLine2())
                .city(patient.getCity())
                .state(patient.getState())
                .postalCode(patient.getPostalCode())
                .country(patient.getCountry())
                .latitude(patient.getLatitude())
                .longitude(patient.getLongitude())
                .nationalId(patient.getNationalId())
                .insuranceMemberId(patient.getInsuranceMemberId())
                .insuranceProviderName(patient.getInsuranceProviderName())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactRelation(patient.getEmergencyContactRelation())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .allergies(patient.getAllergies())
                .medications(patient.getMedications())
                .chronicConditions(patient.getChronicConditions())
                .bloodType(patient.getBloodType())
                .preferredLanguage(patient.getPreferredLanguage())
                .status(patient.getStatus())
                .maritalStatus(patient.getMaritalStatus())
                .consentToShareData(patient.getConsentToShareData())
                .smsOptIn(patient.getSmsOptIn())
                .emailOptIn(patient.getEmailOptIn())
                .notes(patient.getNotes())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                // Flatten User — only safe scalar fields
                .userId(patient.getUser() != null ? patient.getUser().getId() : null)
                .userUsername(patient.getUser() != null ? patient.getUser().getUsername() : null)
                .userEmail(patient.getUser() != null ? patient.getUser().getEmail() : null)
                // Flatten Hospital
                .hospitalId(patient.getHospital() != null ? patient.getHospital().getId() : null)
                .hospitalName(patient.getHospital() != null ? patient.getHospital().getName() : null)
                .build();
    }
}
