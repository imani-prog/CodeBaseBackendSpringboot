package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ReportRequest {
    @NotBlank
    private String type; // CLINICAL, FINANCIAL, OPERATIONAL, AUDIT, INTEGRATION
    @NotBlank
    private String title;
    private String description;

    private Long generatedByUserId; // optional
    private Long hospitalId; // optional

    private OffsetDateTime periodStart; // optional
    private OffsetDateTime periodEnd;   // optional

    private String parameters; // JSON
    private String relatedEntities; // JSON
}

