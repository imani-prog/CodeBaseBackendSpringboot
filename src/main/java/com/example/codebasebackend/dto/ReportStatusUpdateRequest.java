package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportStatusUpdateRequest {
    @NotBlank
    private String status; // PENDING, IN_PROGRESS, COMPLETE, FAILED
    private String errorMessage; // optional
}

