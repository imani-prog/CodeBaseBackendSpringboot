package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
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
@Entity
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "ambulance_dispatches",
        indexes = {
                @Index(name = "idx_dispatch_incident", columnList = "incidentId"),
                @Index(name = "idx_dispatch_status", columnList = "status"),
                @Index(name = "idx_dispatch_priority", columnList = "priority"),
                @Index(name = "idx_dispatch_request_time", columnList = "requestTime"),
                @Index(name = "idx_dispatch_hospital", columnList = "hospital_id"),
                @Index(name = "idx_dispatch_patient", columnList = "patient_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_dispatch_incident_id", columnNames = {"incidentId"})
        }
)
public class AmbulanceDispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Incident identifiers
    @NotBlank
    @Column(nullable = false, length = 64, unique = true)
    private String incidentId; // External or system incident reference

    @Column(length = 80)
    private String incidentType; // e.g., TRAFFIC_ACCIDENT, CARDIAC_ARREST

    // Caller/requester details
    @Column(length = 120)
    private String callerName;

    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    @Column(length = 32)
    private String callerPhone;

    @Column(columnDefinition = "text")
    private String callerNotes;

    // Unit / crew info
    @Column(length = 64)
    private String ambulanceUnitId; // internal unit identifier

    @Column(length = 64)
    private String vehiclePlate;

    @Column(length = 120)
    private String driverName;

    @Column(length = 120)
    private String medicName;

    // Pickup location (either address and/or coordinates)
    @Column(length = 150)
    private String pickupAddressLine1;

    @Column(length = 150)
    private String pickupAddressLine2;

    @Column(length = 80)
    private String pickupCity;

    @Column(length = 80)
    private String pickupState;

    @Column(length = 20)
    private String pickupPostalCode;

    @Column(length = 80)
    private String pickupCountry;

    @Column(precision = 9, scale = 6)
    private BigDecimal pickupLatitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal pickupLongitude;

    // Dropoff - usually a hospital; optional free-form address as fallback
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital; // destination hospital

    @Column(length = 150)
    private String dropoffAddressLine1;

    @Column(length = 150)
    private String dropoffAddressLine2;

    @Column(length = 80)
    private String dropoffCity;

    @Column(length = 80)
    private String dropoffState;

    @Column(length = 20)
    private String dropoffPostalCode;

    @Column(length = 80)
    private String dropoffCountry;

    @Column(precision = 9, scale = 6)
    private BigDecimal dropoffLatitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal dropoffLongitude;

    // Associated patient (optional at request time)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Ambulance information
    @ManyToOne
    @JoinColumn(name = "ambulance_id", nullable = false)
    private Ambulances ambulance;

    // Timings
    @Column(nullable = false)
    private OffsetDateTime requestTime; // when call/request received

    private OffsetDateTime dispatchTime; // crew dispatched
    private OffsetDateTime enRouteTime;  // wheels rolling
    private OffsetDateTime onSceneTime;  // arrived at scene
    private OffsetDateTime departSceneTime; // left scene
    private OffsetDateTime arrivalAtHospitalTime; // reached hospital
    private OffsetDateTime completionTime; // case closed

    // Status and priority
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private DispatchStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private DispatchPriority priority;

    // Clinical/operational notes
    @Column(columnDefinition = "text")
    private String notes;

    // Audit
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersistDefaults() {
        if (status == null) status = DispatchStatus.REQUESTED;
        if (priority == null) priority = DispatchPriority.MEDIUM;
        if (requestTime == null) requestTime = OffsetDateTime.now();
    }

    public enum DispatchStatus {
        REQUESTED, DISPATCHED, EN_ROUTE, ON_SCENE, TRANSPORTING, AT_HOSPITAL, COMPLETED, CANCELED
    }

    public enum DispatchPriority { LOW, MEDIUM, HIGH, CRITICAL }
}
