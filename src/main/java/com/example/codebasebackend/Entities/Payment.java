package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "payments",
        indexes = {
                @Index(name = "idx_payment_billing", columnList = "billing_id"),
                @Index(name = "idx_payment_method", columnList = "method"),
                @Index(name = "idx_payment_status", columnList = "status")
        }
)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "billing_id", nullable = false)
    private Billing billing;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Method method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(length = 3)
    private String currency;

    @Column(length = 64)
    private String externalReference; // transaction id from gateway/insurer

    @Column(columnDefinition = "text")
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public enum Method { CASH, CARD, MOBILE_MONEY, INSURANCE }
    public enum Status { PENDING, COMPLETED, FAILED, REFUNDED }
}

