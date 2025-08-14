package com.example.codebasebackend.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "patients",
        indexes = {
                @Index(name = "idx_patient_last_name", columnList = "lastName"),
                @Index(name = "idx_patient_email", columnList = "email"),
                @Index(name = "idx_patient_national_id", columnList = "nationalId"),
                @Index(name = "idx_patient_hospital", columnList = "hospital_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_patient_email", columnNames = {"email"}),
                @UniqueConstraint(name = "uk_patient_national_id", columnNames = {"nationalId"})
        }
)
public class Patient {
    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identity
    @NotBlank
    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(length = 100)
    private String middleName;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Gender gender;

    @Past(message = "dateOfBirth must be in the past")
    @Column
    private LocalDate dateOfBirth;

    // Contact
    @Email
    @Column(length = 150, unique = true)
    private String email;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    @Column(length = 32)
    private String phone;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    @Column(length = 32)
    private String secondaryPhone;

    // Address
    @Column(length = 150)
    private String addressLine1;

    @Column(length = 150)
    private String addressLine2;

    @Column(length = 80)
    private String city;

    @Column(length = 80)
    private String state;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 80)
    private String country;

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    // Government/insurance
    @Column(length = 64, unique = true)
    private String nationalId;

    @Column(length = 64)
    private String insuranceMemberId;

    @Column(length = 120)
    private String insuranceProviderName;

    // Emergency contact
    @Column(length = 120)
    private String emergencyContactName;

    @Column(length = 80)
    private String emergencyContactRelation;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    @Column(length = 32)
    private String emergencyContactPhone;

    // Clinical profile (free-form text; can be JSON later)
    @Column(columnDefinition = "text")
    private String allergies;

    @Column(columnDefinition = "text")
    private String medications;

    @Column(columnDefinition = "text")
    private String chronicConditions;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private BloodType bloodType;

    // Preferences and status
    @Column(length = 20)
    private String preferredLanguage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PatientStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private MaritalStatus maritalStatus;

    private Boolean consentToShareData;
    private Boolean smsOptIn;
    private Boolean emailOptIn;

    @Column(columnDefinition = "text")
    private String notes;

    // Audit
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @PrePersist
    void prePersistDefaults() {
        if (status == null) status = PatientStatus.ACTIVE;
        if (gender == null) gender = Gender.UNKNOWN;
    }

    // Simple enums kept here to avoid extra files and keep usage clear
    public enum Gender { MALE, FEMALE, OTHER, UNKNOWN }
    public enum BloodType { A_POS, A_NEG, B_POS, B_NEG, AB_POS, AB_NEG, O_POS, O_NEG }
    public enum PatientStatus { ACTIVE, INACTIVE, DECEASED }
    public enum MaritalStatus { SINGLE, MARRIED, DIVORCED, WIDOWED, SEPARATED, OTHER }
}
