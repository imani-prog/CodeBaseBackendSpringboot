package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
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
@Table(name = "hospitals",
        indexes = {
                @Index(name = "idx_hospital_name", columnList = "name"),
                @Index(name = "idx_hospital_city", columnList = "city"),
                @Index(name = "idx_hospital_email", columnList = "email"),
                @Index(name = "idx_hospital_registration", columnList = "registrationNumber")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_hospital_registration", columnNames = {"registrationNumber"})
        }
)
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identity
    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private HospitalType type;

    @NotBlank
    @Column(nullable = false, length = 64, unique = true)
    private String registrationNumber; // Government or licensing registration

    @Column(length = 64)
    private String taxId;

    // Contact
    @Email
    @Column(length = 150)
    private String email;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    @Column(length = 32)
    private String mainPhone;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    @Column(length = 32)
    private String altPhone;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid fax format")
    @Column(length = 32)
    private String fax;

    @Column(length = 200)
    private String website;

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

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    // Administrative contact
    @Column(length = 120)
    private String adminContactName;

    @Email
    @Column(length = 150)
    private String adminContactEmail;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    @Column(length = 32)
    private String adminContactPhone;

    // Capacity and resources
    @Min(0)
    private Integer numberOfBeds;

    @Min(0)
    private Integer numberOfIcuBeds;

    @Min(0)
    private Integer numberOfAmbulances;

    // Offerings and operations
    @Column(columnDefinition = "text")
    private String servicesOffered; // e.g., radiology, surgery, labs

    @Column(columnDefinition = "text")
    private String departments; // e.g., cardiology, oncology

    @Column(columnDefinition = "text")
    private String operatingHours; // e.g., JSON or freeform

    @Column(columnDefinition = "text")
    private String acceptedInsurance; // freeform list

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private HospitalStatus status;

    @Column(columnDefinition = "text")
    private String notes;

    // Relationships
    @Builder.Default
    @OneToMany(mappedBy = "hospital", fetch = FetchType.LAZY)
    private Set<Patient> patients = new HashSet<>();

    // Audit
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersistDefaults() {
        if (status == null) status = HospitalStatus.ACTIVE;
        if (type == null) type = HospitalType.GENERAL;
    }

    public enum HospitalType { GENERAL, CLINIC, SPECIALTY, TEACHING, REHABILITATION, EMERGENCY_CENTER }
    public enum HospitalStatus { ACTIVE, INACTIVE, UNDER_MAINTENANCE, CLOSED }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Hospital hospital = (Hospital) o;
        return getId() != null && Objects.equals(getId(), hospital.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
