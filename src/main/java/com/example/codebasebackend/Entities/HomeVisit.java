package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
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
@Entity
@Table(name = "home_visits",
        indexes = {
                @Index(name = "idx_home_visit_patient", columnList = "patient_id"),
                @Index(name = "idx_home_visit_chw", columnList = "chw_id"),
                @Index(name = "idx_home_visit_status", columnList = "status"),
                @Index(name = "idx_home_visit_scheduled", columnList = "scheduledAt")
        }
)
public class HomeVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chw_id")
    private CommunityHealthWorkers chw;

    @Column(length = 120)
    private String visitType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private Priority priority;

    private OffsetDateTime scheduledAt;
    private OffsetDateTime completedAt;
    private OffsetDateTime canceledAt;

    @Column(length = 255)
    private String location;

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(columnDefinition = "text")
    private String reason;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(columnDefinition = "text")
    private String outcome;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersistDefaults() {
        if (status == null) status = Status.SCHEDULED;
        if (visitType == null || visitType.isBlank()) visitType = "Home Visit";
        if (priority == null) priority = Priority.NORMAL;
    }

    public enum Status { SCHEDULED, IN_PROGRESS, COMPLETED, CANCELED, NO_SHOW }
    public enum Priority { NORMAL, HIGH, URGENT }
}

