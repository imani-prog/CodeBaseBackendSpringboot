package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "service_order_items",
        indexes = {
                @Index(name = "idx_service_order_item_order", columnList = "order_id")
        }
)
public class ServiceOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private ServiceOrder order;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String serviceName;

    @Column(length = 64)
    private String serviceCode;

    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal lineSubtotal; // quantity * unitPrice

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal lineTax; // optional tax per line

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(precision = 14, scale = 2)
    private BigDecimal lineTotal; // lineSubtotal + lineTax

    @PrePersist
    @PreUpdate
    void computeDerived() {
        BigDecimal qty = BigDecimal.valueOf(quantity == null ? 0 : quantity);
        BigDecimal price = unitPrice == null ? BigDecimal.ZERO : unitPrice;
        lineSubtotal = qty.multiply(price);
        if (lineTax == null) lineTax = BigDecimal.ZERO;
        lineTotal = lineSubtotal.add(lineTax);
    }
}

