package com.example.codebasebackend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public class RescheduleRequest {
    @NotNull
    @FutureOrPresent
    private OffsetDateTime newStart;

    @NotNull
    @Future
    private OffsetDateTime newEnd;

    public OffsetDateTime getNewStart() { return newStart; }
    public void setNewStart(OffsetDateTime newStart) { this.newStart = newStart; }
    public OffsetDateTime getNewEnd() { return newEnd; }
    public void setNewEnd(OffsetDateTime newEnd) { this.newEnd = newEnd; }
}
