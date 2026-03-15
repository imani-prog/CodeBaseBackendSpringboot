package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "prescription_refill_requests",
        indexes = {
                @Index(name = "idx_refill_status", columnList = "status"),
                @Index(name = "idx_refill_prescription", columnList = "prescription_id"),
                @Index(name = "idx_refill_pharmacy", columnList = "pharmacy_id")
        }
)
public class PrescriptionRefillRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RefillStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryMethod deliveryMethod;

    @Column(length = 500)
    private String additionalInstructions;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime requestedAt;

    private OffsetDateTime decidedAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void defaults() {
        if (status == null) status = RefillStatus.PENDING;
        if (deliveryMethod == null) deliveryMethod = DeliveryMethod.HOME_DELIVERY;
    }

    public enum RefillStatus { PENDING, APPROVED, REJECTED, FULFILLED }
    public enum DeliveryMethod { HOME_DELIVERY, PICK_UP }
}

