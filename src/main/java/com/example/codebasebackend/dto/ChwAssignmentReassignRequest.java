package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotNull;

public class ChwAssignmentReassignRequest {
    @NotNull
    private Long chwId;

    private String reason;

    public Long getChwId() { return chwId; }
    public void setChwId(Long chwId) { this.chwId = chwId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

