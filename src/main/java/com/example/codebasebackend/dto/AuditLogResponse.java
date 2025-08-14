package com.example.codebasebackend.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AuditLogResponse {
    private Long id;
    private String eventType;
    private String entityType;
    private String entityId;
    private Long userId;
    private String username;
    private String ipAddress;
    private OffsetDateTime eventTime;

    private String status;
    private String errorMessage;

    private Long integrationPartnerId;
    private String sessionId;
    private String correlationId;
    private String userAgent;

    private String details;
    private OffsetDateTime updatedAt;
}

