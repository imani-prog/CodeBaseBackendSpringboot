package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "insurance_providers",
        indexes = {
                @Index(name = "idx_provider_name", columnList = "name"),
                @Index(name = "idx_provider_payer_id", columnList = "payerId")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_provider_name", columnNames = {"name"}),
                @UniqueConstraint(name = "uk_provider_payer_id", columnNames = {"payerId"})
        }
)
public class InsuranceProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 32)
    private String payerId; // EDI payer id if available

    @Column(length = 64)
    private String registrationNumber;

    // Contacts
    @Email
    @Column(length = 150)
    private String email;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$")
    @Column(length = 32)
    private String phone;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$")
    @Column(length = 32)
    private String fax;

    @Column(length = 200)
    private String website;

    @Column(length = 200)
    private String providerPortalUrl;

    @Column(length = 200)
    private String claimsSubmissionUrl;

    @Email
    @Column(length = 150)
    private String claimsSubmissionEmail;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$")
    @Column(length = 32)
    private String supportPhone;

    @Email
    @Column(length = 150)
    private String supportEmail;

    // Address
    @Column(length = 150)
    private String addressLine1;

    @Column(length = 150)
    private String addressLine2;

    @Column(length = 80)
    private String city;

    @Column(length = 80)
    private String state;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 80)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ProviderStatus status;

    @Column(columnDefinition = "text")
    private String notes;

    // Relationships
    @Builder.Default
    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    private Set<InsurancePlan> plans = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    private Set<PatientInsurancePolicy> policies = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_partner_id")
    private IntegrationPartner integrationPartner;

    // Audit
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersistDefaults() {
        if (status == null) status = ProviderStatus.ACTIVE;
    }

    public enum ProviderStatus { ACTIVE, INACTIVE }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        InsuranceProvider that = (InsuranceProvider) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
