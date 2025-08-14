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
@Table(name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_event_type", columnList = "eventType"),
                @Index(name = "idx_audit_entity_type", columnList = "entityType"),
                @Index(name = "idx_audit_user_id", columnList = "user_id"),
                @Index(name = "idx_audit_event_time", columnList = "eventTime")
        }
)
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private EventType eventType; // LOGIN, LOGOUT, READ, CREATE, UPDATE, DELETE, EXPORT, INTEGRATION, etc.

    @Column(length = 80)
    private String entityType; // e.g., Patient, Billing, InsuranceClaim

    @Column(length = 64)
    private String entityId; // String for flexibility

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Null for system/service events

    @Column(length = 120)
    private String username; // username or service name

    @Column(length = 64)
    private String ipAddress;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime eventTime; // stored in UTC by default

    @Lob
    private String details; // JSON/Text content

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private EventStatus status; // SUCCESS, FAILURE, PENDING

    @Column(length = 500)
    private String errorMessage;

    @Column
    private Long integrationPartnerId; // optional

    @Column(length = 120)
    private String sessionId; // optional

    @Column(length = 120)
    private String correlationId; // optional request id

    @Column(length = 200)
    private String userAgent; // optional

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public enum EventType { LOGIN, LOGOUT, READ, CREATE, UPDATE, DELETE, EXPORT, INTEGRATION, PERMISSION_CHANGE, CONFIG_CHANGE, ERROR }
    public enum EventStatus { SUCCESS, FAILURE, PENDING }
}
