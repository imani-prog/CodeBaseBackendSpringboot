package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "billings",
        indexes = {
                @Index(name = "idx_billing_invoice", columnList = "invoiceNumber"),
                @Index(name = "idx_billing_patient", columnList = "patient_id"),
                @Index(name = "idx_billing_hospital", columnList = "hospital_id"),
                @Index(name = "idx_billing_status", columnList = "status"),
                @Index(name = "idx_billing_partner", columnList = "integration_partner_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_invoice_number", columnNames = {"invoiceNumber"})
        }
)
public class Billing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 64, unique = true)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(nullable = false)
    private OffsetDateTime issueDate;

    private OffsetDateTime serviceDate;

    private OffsetDateTime dueDate;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal subtotal;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal discount;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal tax;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal total;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal amountPaid;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal balance;

    @Column(length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private InvoiceStatus status;

    @Column(columnDefinition = "text")
    private String notes;

    @Builder.Default
    @OneToMany(mappedBy = "billing", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Payment> payments = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "billing", fetch = FetchType.LAZY)
    private Set<InsuranceClaim> insuranceClaims = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_partner_id")
    private IntegrationPartner integrationPartner;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersistDefaults() {
        if (issueDate == null) issueDate = OffsetDateTime.now();
        if (status == null) status = InvoiceStatus.ISSUED;
        if (currency == null) currency = "KES";
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (discount == null) discount = BigDecimal.ZERO;
        if (tax == null) tax = BigDecimal.ZERO;
        if (total == null) total = BigDecimal.ZERO;
        if (amountPaid == null) amountPaid = BigDecimal.ZERO;
        if (balance == null) balance = total.subtract(amountPaid);
    }

    public enum InvoiceStatus { DRAFT, ISSUED, PARTIALLY_PAID, PAID, CANCELED, WRITEOFF }
}
