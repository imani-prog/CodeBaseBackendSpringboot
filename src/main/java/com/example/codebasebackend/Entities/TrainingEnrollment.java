
package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "training_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chw_module",
                        columnNames = {"chw_id", "training_module_id"}
                )
        },
        indexes = {
                @Index(name = "idx_enrollment_chw",    columnList = "chw_id"),
                @Index(name = "idx_enrollment_module", columnList = "training_module_id"),
                @Index(name = "idx_enrollment_status", columnList = "status"),
                @Index(name = "idx_enrollment_date",   columnList = "enrolled_at")
        }
)
public class TrainingEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chw_id", nullable = false)
    private CommunityHealthWorkers chw;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_module_id", nullable = false)
    private TrainingModule trainingModule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(name = "progress_percentage")
    @Builder.Default
    private int progressPercentage = 0;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "certificate_issued")
    @Builder.Default
    private boolean certificateIssued = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum EnrollmentStatus {
        ENROLLED,
        IN_PROGRESS,
        COMPLETED,
        DROPPED,
        SUSPENDED
    }
}