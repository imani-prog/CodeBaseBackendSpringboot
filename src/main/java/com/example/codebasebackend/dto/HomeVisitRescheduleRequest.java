package com.example.codebasebackend.dto;

import java.time.OffsetDateTime;

public class HomeVisitRescheduleRequest {
    private OffsetDateTime scheduledAt;
    private String reason;

    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(OffsetDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

