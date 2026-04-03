package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotBlank;

public class ChwAssignmentStatusUpdateRequest {
    @NotBlank
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

