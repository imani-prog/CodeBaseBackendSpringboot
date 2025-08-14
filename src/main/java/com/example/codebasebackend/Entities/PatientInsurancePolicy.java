package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "patient_insurance_policies",
        indexes = {
                @Index(name = "idx_policy_patient", columnList = "patient_id"),
                @Index(name = "idx_policy_provider", columnList = "provider_id"),
                @Index(name = "idx_policy_member", columnList = "memberId")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_provider", columnNames = {"provider_id", "memberId"})
        }
)
public class PatientInsurancePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private InsuranceProvider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private InsurancePlan plan; // optional

    @NotBlank
    @Column(nullable = false, length = 64)
    private String memberId;

    @Column(length = 64)
    private String groupNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CoverageLevel coverageLevel;

    @Column(length = 120)
    private String policyholderName;

    @Column(length = 80)
    private String policyholderRelation;

    private LocalDate policyholderDob;

    @NotNull
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PolicyStatus status;

    @Column(length = 200)
    private String cardFrontUrl;

    @Column(length = 200)
    private String cardBackUrl;

    @Column(columnDefinition = "text")
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersistDefaults() {
        if (coverageLevel == null) coverageLevel = CoverageLevel.PRIMARY;
        if (status == null) status = PolicyStatus.ACTIVE;
    }

    public enum CoverageLevel { PRIMARY, SECONDARY, TERTIARY }
    public enum PolicyStatus { ACTIVE, INACTIVE, TERMINATED }
}

