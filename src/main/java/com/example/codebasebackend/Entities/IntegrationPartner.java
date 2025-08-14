package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "integration_partners",
        indexes = {
                @Index(name = "idx_partner_name", columnList = "name"),
                @Index(name = "idx_partner_type", columnList = "type"),
                @Index(name = "idx_partner_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_partner_name", columnNames = {"name"})
        }
)
public class IntegrationPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PartnerType type; // INSURANCE, PAYMENT_GATEWAY, HEALTH_EXCHANGE, OTHER

    @Column(length = 200)
    private String apiUrl;

    @Column(length = 256)
    private String apiKey; // store securely (encrypted at rest)

    @Email
    @Column(length = 150)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PartnerStatus status; // ACTIVE, INACTIVE, SUSPENDED

    @Column(columnDefinition = "text")
    private String metadata; // JSON-like free config

    @Builder.Default
    @OneToMany(mappedBy = "integrationPartner", fetch = FetchType.LAZY)
    private Set<InsuranceProvider> insuranceProviders = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "integrationPartner", fetch = FetchType.LAZY)
    private Set<InsuranceClaim> insuranceClaims = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "integrationPartner", fetch = FetchType.LAZY)
    private Set<Billing> billings = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public enum PartnerType { INSURANCE, PAYMENT_GATEWAY, HEALTH_EXCHANGE, OTHER }
    public enum PartnerStatus { ACTIVE, INACTIVE, SUSPENDED }
}

