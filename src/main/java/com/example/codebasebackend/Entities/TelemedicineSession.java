package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "telemedicine_sessions",
        indexes = {
                @Index(name = "idx_session_id", columnList = "sessionId"),
                @Index(name = "idx_session_status", columnList = "status"),
                @Index(name = "idx_session_patient", columnList = "patient_id"),
                @Index(name = "idx_session_doctor", columnList = "doctor_id"),
                @Index(name = "idx_session_hospital", columnList = "hospital_id"),
                @Index(name = "idx_session_start_time", columnList = "startTime"),
                @Index(name = "idx_session_platform", columnList = "platform"),
                @Index(name = "idx_session_priority", columnList = "priority"),
                @Index(name = "idx_session_created_at", columnList = "createdAt")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_session_id", columnNames = {"sessionId"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelemedicineSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Session ID is required")
    @Column(nullable = false, unique = true, length = 20)
    private String sessionId; // Format: TM-001, TM-002, etc.

    // Patient Information
    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Doctor Information
    @NotNull(message = "Doctor is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Hospital/Clinic (optional - for institutional sessions)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    // Session Details
    @NotNull(message = "Session type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SessionType sessionType;

    @NotNull(message = "Platform type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlatformType platform;

    @NotNull(message = "Session status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status = SessionStatus.SCHEDULED;

    @NotNull(message = "Priority is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority = Priority.NORMAL;

    // Timing
    @NotNull(message = "Start time is required")
    @Column(nullable = false)
    private OffsetDateTime startTime;

    @Column
    private OffsetDateTime actualStartTime; // When session actually started

    @Column
    private OffsetDateTime endTime;

    @Min(value = 0, message = "Duration must be non-negative")
    @Column
    private Integer duration; // in minutes

    @Min(value = 0, message = "Planned duration must be positive")
    @Column
    private Integer plannedDuration; // Expected duration in minutes

    // Medical Information
    @ElementCollection
    @CollectionTable(name = "session_symptoms", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "symptom", length = 200)
    private List<String> symptoms = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String chiefComplaint;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String prescription;

    @Column(columnDefinition = "TEXT")
    private String doctorNotes;

    @Column
    private Boolean followUpRequired = false;

    @Column
    private OffsetDateTime followUpDate;

    // Financial
    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cost must be non-negative")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(precision = 10, scale = 2)
    private BigDecimal actualCost; // May differ from planned cost

    @Column(length = 20)
    private String paymentStatus = "PENDING"; // PENDING, PAID, REFUNDED

    @Column(length = 50)
    private String paymentReference;

    // Quality & Feedback
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Column
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    // Technical Details
    @Column(length = 200)
    private String meetingLink; // Virtual meeting room URL

    @Column(length = 100)
    private String meetingId;

    @Column(length = 200)
    private String recordingUrl;

    @Column
    private Boolean recordingEnabled = false;

    // Session Metadata
    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    @Column
    private OffsetDateTime cancelledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by_user_id")
    private User cancelledBy;

    @Column
    private Boolean reminderSent = false;

    @Column
    private OffsetDateTime reminderSentAt;

    // Audit Fields
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    // Helper Methods
    public void startSession() {
        this.status = SessionStatus.ACTIVE;
        this.actualStartTime = OffsetDateTime.now();
    }

    public void pauseSession() {
        if (this.status == SessionStatus.ACTIVE) {
            this.status = SessionStatus.PAUSED;
        }
    }

    public void resumeSession() {
        if (this.status == SessionStatus.PAUSED) {
            this.status = SessionStatus.ACTIVE;
        }
    }

    public void completeSession() {
        this.status = SessionStatus.COMPLETED;
        this.endTime = OffsetDateTime.now();
        if (this.actualStartTime != null) {
            this.duration = (int) java.time.Duration.between(actualStartTime, endTime).toMinutes();
        }
    }

    public void cancelSession(User cancelledBy, String reason) {
        this.status = SessionStatus.CANCELLED;
        this.cancelledAt = OffsetDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancellationReason = reason;
    }

    public void terminateSession(User terminatedBy, String reason) {
        this.status = SessionStatus.TERMINATED;
        this.endTime = OffsetDateTime.now();
        this.cancelledBy = terminatedBy;
        this.cancellationReason = reason;
    }
}
