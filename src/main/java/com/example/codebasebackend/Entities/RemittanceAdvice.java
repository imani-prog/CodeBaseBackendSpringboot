package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "remittance_advices",
        indexes = {
                @Index(name = "idx_remit_claim", columnList = "claim_id"),
                @Index(name = "idx_remit_date", columnList = "remittanceDate")
        }
)
public class RemittanceAdvice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private InsuranceClaim claim;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal amountPaid;

    @Column(length = 64)
    private String payerReference;

    private OffsetDateTime remittanceDate;

    @Column(columnDefinition = "text")
    private String adjustments; // JSON or free text explaining adjustments

    @Column(columnDefinition = "text")
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}

