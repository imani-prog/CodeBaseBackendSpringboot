package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "insurance_claims",
        indexes = {
                @Index(name = "idx_claim_billing", columnList = "billing_id"),
                @Index(name = "idx_claim_provider", columnList = "provider_id"),
                @Index(name = "idx_claim_status", columnList = "status"),
                @Index(name = "idx_claim_number", columnList = "claimNumber"),
                @Index(name = "idx_claim_partner", columnList = "integration_partner_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_claim_number", columnNames = {"claimNumber"})
        }
)
public class InsuranceClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "billing_id", nullable = false)
    private Billing billing;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private InsuranceProvider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private PatientInsurancePolicy policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private InsurancePlan plan;

    @Column(length = 64, unique = true)
    private String claimNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ClaimStatus status;

    private OffsetDateTime submissionDate;
    private OffsetDateTime responseDate;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal claimedAmount;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal approvedAmount;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal patientResponsibility;

    @Column(columnDefinition = "text")
    private String rejectionReason;

    @Builder.Default
    @OneToMany(mappedBy = "claim", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RemittanceAdvice> remittances = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_partner_id")
    private IntegrationPartner integrationPartner;

    public enum ClaimStatus { DRAFT, SUBMITTED, PENDING, APPROVED, REJECTED, PAID, CANCELED }
}
