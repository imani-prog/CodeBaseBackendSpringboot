package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "pharmacies",
        indexes = {
                @Index(name = "idx_pharmacy_name", columnList = "name"),
                @Index(name = "idx_pharmacy_city", columnList = "city")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pharmacy_name_address", columnNames = {"name", "address"})
        }
)
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 180)
    private String name;

    @Column(length = 200)
    private String address;

    @Column(length = 80)
    private String city;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 32)
    private String phone;

    @Column(length = 160)
    private String hours;

    @Column(length = 40)
    private String distanceText;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    @Column(columnDefinition = "DECIMAL(2,1)")
    private Double rating;

    @Column(length = 40)
    private String deliveryFee;

    @Column(length = 40)
    private String estimatedDelivery;

    private Boolean nhifAccepted;

    private Boolean offersDelivery;

    @ElementCollection
    @CollectionTable(name = "pharmacy_services", joinColumns = @JoinColumn(name = "pharmacy_id"))
    @Column(name = "service", length = 120)
    private List<String> services = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}

