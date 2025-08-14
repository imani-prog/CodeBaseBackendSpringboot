package com.example.codebasebackend.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ReportResponse {
    private Long id;
    private String type;
    private String title;
    private String description;
    private Long generatedByUserId;
    private Long hospitalId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime periodStart;
    private OffsetDateTime periodEnd;
    private String parameters;
    private String status;
    private String fileUrl;
    private String rawData;
    private String errorMessage;
    private String relatedEntities;
}

