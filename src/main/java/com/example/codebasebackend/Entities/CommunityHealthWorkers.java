package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@Table(name = "community_health_workers",
        indexes = {
                @Index(name = "idx_chw_status", columnList = "status"),
                @Index(name = "idx_chw_city", columnList = "city"),
                @Index(name = "idx_chw_code", columnList = "code")
        }
)
public class CommunityHealthWorkers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 16, unique = true)
    private String code; // CHW001-style business id



    // Identity
    @NotBlank
    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(length = 100)
    private String middleName;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String lastName;

    // Contact
    @Email
    @Column(length = 150)
    private String email;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    @Column(length = 32)
    private String phone;

    // Address (optional; geolocation preferred for proximity)
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

    // Geolocation
    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    // Affiliation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital; // optional base hospital/clinic

    // Operational
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @Column(length = 200)
    private String specialization; // optional freeform, e.g., maternal health

    // Audit
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersistDefaults() {
        if (status == null) status = Status.AVAILABLE;
    }

    public enum Status { AVAILABLE, BUSY, OFFLINE }
}
