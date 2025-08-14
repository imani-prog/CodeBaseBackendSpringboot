 package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuditLogRequest {
    @NotBlank
    private String eventType; // LOGIN, READ, CREATE, UPDATE, DELETE, EXPORT, INTEGRATION, etc.
    private String entityType;
    private String entityId; // String for flexibility

    private Long userId; // optional
    private String username; // optional

    private String ipAddress; // optional override
    private String sessionId; // optional
    private String correlationId; // optional
    private String userAgent; // optional

    private String status; // SUCCESS, FAILURE, PENDING (optional, default SUCCESS)

    @Size(max = 500)
    private String errorMessage; // optional

    private Long integrationPartnerId; // optional

    private String details; // JSON/Text, will be sanitized
}

