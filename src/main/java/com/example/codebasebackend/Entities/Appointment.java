package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "appointments",
        indexes = {
                @Index(name = "idx_appt_status", columnList = "status"),
                @Index(name = "idx_appt_start_time", columnList = "scheduledStart"),
                @Index(name = "idx_appt_patient", columnList = "patient_id"),
                @Index(name = "idx_appt_hospital", columnList = "hospital_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_appt_code", columnNames = {"appointmentCode"})
        }
)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Business identity
    @Column(length = 64, unique = true)
    private String appointmentCode; // external or human-friendly code

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    // Scheduling
    @NotNull
    @FutureOrPresent(message = "scheduledStart must be in the present or future")
    @Column(nullable = false)
    private OffsetDateTime scheduledStart;

    @NotNull
    @Future(message = "scheduledEnd must be in the future")
    @Column(nullable = false)
    private OffsetDateTime scheduledEnd;

    private OffsetDateTime checkInTime;
    private OffsetDateTime checkOutTime;

    // Classification
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 24)
    private AppointmentType type;

    // Clinical/operational
    @Column(length = 150)
    private String providerName; // e.g., doctor/clinician name if no User entity yet

    @Column(length = 80)
    private String room;

    @Column(length = 120)
    private String location; // clinic or department display name

    @Column(columnDefinition = "text")
    private String reason;

    @Column(columnDefinition = "text")
    private String notes;

    private Boolean reminderSent;

    // Audit
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersistDefaults() {
        if (status == null) status = AppointmentStatus.SCHEDULED;
        if (scheduledEnd != null && scheduledStart != null && scheduledEnd.isBefore(scheduledStart)) {
            // Basic guard; in practice, enforce via service-level validation
            scheduledEnd = scheduledStart.plusMinutes(30);
        }
    }

    public enum AppointmentStatus { SCHEDULED, CHECKED_IN, IN_PROGRESS, COMPLETED, CANCELED, NO_SHOW, RESCHEDULED }
    public enum AppointmentType { CONSULTATION, FOLLOW_UP, SURGERY, LAB_TEST, IMAGING, VACCINATION, TELEHEALTH, OTHER }
}
