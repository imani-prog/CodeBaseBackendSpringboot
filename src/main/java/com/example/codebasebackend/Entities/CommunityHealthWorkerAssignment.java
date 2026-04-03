package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "chw_assignments",
        indexes = {
                @Index(name = "idx_chw_assignment_status", columnList = "status"),
                @Index(name = "idx_chw_assignment_patient", columnList = "patient_id"),
                @Index(name = "idx_chw_assignment_chw", columnList = "chw_id"),
                @Index(name = "idx_chw_assignment_type", columnList = "assignmentType"),
                @Index(name = "idx_chw_assignment_appointment", columnList = "appointment_id")
        }
)
public class CommunityHealthWorkerAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chw_id")
    private CommunityHealthWorkers chw;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AssignmentType assignmentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    private OffsetDateTime assignedAt;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;

    @Column(columnDefinition = "text")
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersistDefaults() {
        if (status == null) status = Status.ASSIGNED;
        if (assignedAt == null) assignedAt = OffsetDateTime.now();
        if (assignmentType == null) assignmentType = AssignmentType.TASK;
    }

    public enum Status { ASSIGNED, IN_PROGRESS, COMPLETED, CANCELED }
    public enum AssignmentType { TASK, HOME_VISIT, APPOINTMENT }
}

