package com.example.codebasebackend.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    private String code;

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
    private String region;

    // Workload Management
    @Column
    private Integer assignedPatients;

    @Column
    private LocalDate startDate;

    @Column
    private OffsetDateTime lastStatusUpdate;

    // Performance Metrics
    @Column
    private Integer monthlyVisits;

    @Column(precision = 5, scale = 2)
    private BigDecimal successRate;

    @Column(length = 50)
    private String responseTime;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    // Affiliation
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "passwordHash"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", unique = true)
    private User user;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    // Operational
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @Column(length = 200)
    private String specialization;

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
        if (lastStatusUpdate == null) {
            lastStatusUpdate = OffsetDateTime.now();
        }
    }

    public enum Status { AVAILABLE, BUSY, OFFLINE }
}