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
                @Index(name = "idx_chw_assignment_chw", columnList = "chw_id")
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    private OffsetDateTime assignedAt;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;

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
    }

    public enum Status { ASSIGNED, IN_PROGRESS, COMPLETED, CANCELED }
}

