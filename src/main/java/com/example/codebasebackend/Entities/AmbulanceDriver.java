package com.example.codebasebackend.Entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@jakarta.persistence.Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "ambulance_drivers",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_driver_license", columnNames = {"license_number"}),
        @UniqueConstraint(name = "uk_driver_email", columnNames = {"email"})
    },
    indexes = {
        @Index(name = "idx_driver_status", columnList = "status"),
        @Index(name = "idx_driver_name", columnList = "name"),
        @Index(name = "idx_driver_phone", columnList = "phone")
    }
)
public class AmbulanceDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(nullable = false, length = 32)
    private String phone;

    @Column(unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    @Builder.Default
    private DriverStatus status = DriverStatus.OFF_DUTY;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(columnDefinition = "text")
    private String certificationsJson; // JSON array of certifications

    @ManyToOne
    @JoinColumn(name = "current_ambulance_id")
    private Ambulances currentAmbulance;

    @Column(name = "shift_start")
    private String shiftStart; // e.g., "06:00"

    @Column(name = "shift_end")
    private String shiftEnd; // e.g., "18:00"

    @Column(name = "total_trips")
    @Builder.Default
    private Integer totalTrips = 0;

    @Column
    private Double rating; // e.g., 4.85

    @Column(name = "last_trip_time")
    private OffsetDateTime lastTripTime;

    @Column(name = "emergency_contact", length = 32)
    private String emergencyContact;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public enum DriverStatus {
        ON_DUTY,
        OFF_DUTY,
        ON_TRIP,
        ON_BREAK,
        UNAVAILABLE
    }

    // Helper methods for certifications
    @Transient
    public List<String> getCertificationsList() {
        if (certificationsJson == null || certificationsJson.isEmpty()) return new ArrayList<>();
        try {
            return new ObjectMapper().readValue(certificationsJson, new TypeReference<List<String>>(){});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public void setCertificationsList(List<String> certifications) {
        try {
            this.certificationsJson = new ObjectMapper().writeValueAsString(certifications);
        } catch (JsonProcessingException e) {
            this.certificationsJson = null;
        }
    }
}
