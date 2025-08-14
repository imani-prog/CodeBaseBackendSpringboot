package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@jakarta.persistence.Entity
@Table(name = "insurance_plans",
        indexes = {
                @Index(name = "idx_plan_provider", columnList = "provider_id"),
                @Index(name = "idx_plan_code", columnList = "planCode")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_provider_plan_code", columnNames = {"provider_id", "planCode"})
        }
)
public class InsurancePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private InsuranceProvider provider;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String planName;

    @NotBlank
    @Column(nullable = false, length = 64)
    private String planCode; // payer-specific identifier

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PlanType planType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private NetworkType networkType;

    @Column(columnDefinition = "text")
    private String coverageDetails; // freeform: covered services, exclusions, etc.

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 12, scale = 2)
    private BigDecimal deductibleIndividual;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 12, scale = 2)
    private BigDecimal deductibleFamily;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 12, scale = 2)
    private BigDecimal oopMaxIndividual;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 12, scale = 2)
    private BigDecimal oopMaxFamily;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 12, scale = 2)
    private BigDecimal copayPrimaryCare;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 12, scale = 2)
    private BigDecimal copaySpecialist;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 12, scale = 2)
    private BigDecimal copayEmergency;

    @Min(0)
    @Max(100)
    private Integer coinsurancePercent; // percentage patient owes after deductible

    private Boolean requiresReferral;
    private Boolean preauthRequired;

    private OffsetDateTime effectiveFrom;
    private OffsetDateTime effectiveTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PlanStatus status;

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
        if (status == null) status = PlanStatus.ACTIVE;
        if (planType == null) planType = PlanType.OTHER;
        if (networkType == null) networkType = NetworkType.BOTH;
    }

    public enum PlanType { HMO, PPO, EPO, POS, MEDICARE, MEDICAID, SELF_PAY, OTHER }
    public enum NetworkType { IN_NETWORK, OUT_OF_NETWORK, BOTH }
    public enum PlanStatus { ACTIVE, INACTIVE }
}

