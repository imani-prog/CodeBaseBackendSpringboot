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
@Table(name = "reports",
        indexes = {
                @Index(name = "idx_reports_type", columnList = "type"),
                @Index(name = "idx_reports_status", columnList = "status"),
                @Index(name = "idx_reports_created_at", columnList = "createdAt")
        }
)
public class Reports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ReportType type; // CLINICAL, FINANCIAL, OPERATIONAL, AUDIT, INTEGRATION

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User generatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital; // optional hospital association

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime periodStart;
    private OffsetDateTime periodEnd;

    @Lob
    private String parameters; // JSON for filters/fields

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ReportStatus status; // PENDING, IN_PROGRESS, COMPLETE, FAILED

    @Column(length = 500)
    private String fileUrl; // path to exported file

    @Lob
    private String rawData; // optional cached data

    @Column(length = 500)
    private String errorMessage;

    @Lob
    private String relatedEntities; // JSON: e.g., {"patientId":5,"billingIds":[1,2]}

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public enum ReportType { CLINICAL, FINANCIAL, OPERATIONAL, AUDIT, INTEGRATION }
    public enum ReportStatus { PENDING, IN_PROGRESS, COMPLETE, FAILED }
}
