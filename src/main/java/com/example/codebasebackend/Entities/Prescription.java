package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "prescriptions",
        indexes = {
                @Index(name = "idx_prescription_status", columnList = "status"),
                @Index(name = "idx_prescription_patient", columnList = "patient_id"),
                @Index(name = "idx_prescription_end_date", columnList = "endDate")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_prescription_code", columnNames = {"prescriptionCode"})
        }
)
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, unique = true)
    private String prescriptionCode;

    @NotBlank
    @Column(nullable = false, length = 160)
    private String medicationName;

    @Column(length = 160)
    private String genericName;

    @Column(length = 80)
    private String dosage;

    @Column(length = 120)
    private String frequency;

    @Column(columnDefinition = "text")
    private String instructions;

    @Column(columnDefinition = "text")
    private String purpose;

    @Column(columnDefinition = "text")
    private String warnings;

    @ElementCollection
    @CollectionTable(name = "prescription_side_effects", joinColumns = @JoinColumn(name = "prescription_id"))
    @Column(name = "side_effect", length = 180)
    private List<String> sideEffects = new ArrayList<>();

    @NotNull
    @PastOrPresent
    private LocalDate prescribedDate;

    @NotNull
    @PastOrPresent
    private LocalDate startDate;

    private LocalDate endDate;

    @Min(0)
    @Column(nullable = false)
    private Integer totalRefills;

    @Min(0)
    @Column(nullable = false)
    private Integer refillsRemaining;

    @Min(0)
    @Max(100)
    private Integer progressPercent;

    private OffsetDateTime nextDoseAt;

    private Boolean reminderEnabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrescriptionStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor prescribedBy;

    @Column(length = 120)
    private String providerSpecialty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy preferredPharmacy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void initDefaults() {
        if (status == null) status = PrescriptionStatus.ACTIVE;
        if (totalRefills == null) totalRefills = 0;
        if (refillsRemaining == null) refillsRemaining = totalRefills;
        if (progressPercent == null) progressPercent = 0;
    }

    @PreUpdate
    void enforceRefillBounds() {
        if (refillsRemaining != null && refillsRemaining < 0) {
            refillsRemaining = 0;
        }
        if (refillsRemaining != null && totalRefills != null && refillsRemaining > totalRefills) {
            refillsRemaining = totalRefills;
        }
    }

    public enum PrescriptionStatus { ACTIVE, COMPLETED, EXPIRED, CANCELED, REFILL_PENDING }
}


