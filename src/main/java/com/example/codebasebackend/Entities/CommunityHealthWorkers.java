package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                @Index(name = "idx_chw_code", columnList = "code"),
                @Index(name = "idx_chw_region", columnList = "region"),
                @Index(name = "idx_chw_start_date", columnList = "startDate")
        }
)
public class CommunityHealthWorkers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 16, unique = true)
    private String code; // CHW001-style business id


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

    // Geolocation
    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    // Regional Assignment
    @Column(length = 100)
    private String region; // Regional assignment (e.g., "Nairobi", "Mombasa", "Kisumu")

    // Workload Management
    @Column
    private Integer assignedPatients; // Number of patients assigned to this CHW (default: 0)

    @Column
    private LocalDate startDate; // Employment/service start date

    @Column
    private OffsetDateTime lastStatusUpdate; // Timestamp when status was last changed

    // Performance Metrics
    @Column
    private Integer monthlyVisits; // Number of visits this month (default: 0)

    @Column(precision = 5, scale = 2)
    private BigDecimal successRate; // Success rate percentage (0-100)

    @Column(length = 50)
    private String responseTime; // Average response time (e.g., "1.8hrs", "2.5hrs")

    @Column(precision = 3, scale = 2)
    private BigDecimal rating; // Rating out of 5.0 (e.g., 4.8)

    // Affiliation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital; // optional base hospital/clinic

    // Operational
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @Column(length = 200)
    private String specialization; // optional e.g., maternal health

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
        if (assignedPatients == null) assignedPatients = 0;
        if (monthlyVisits == null) monthlyVisits = 0;
        if (successRate == null) successRate = BigDecimal.ZERO;
        if (rating == null) rating = BigDecimal.ZERO;
        if (lastStatusUpdate == null) lastStatusUpdate = OffsetDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        // Automatically update lastStatusUpdate when entity changes
        // Status-specific tracking can be done in service layer
        if (lastStatusUpdate == null) {
            lastStatusUpdate = OffsetDateTime.now();
        }
    }

    public enum Status { AVAILABLE, BUSY, OFFLINE }
}
