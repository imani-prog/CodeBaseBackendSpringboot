package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotBlank;

public class RefillDecisionRequest {
    @NotBlank
    private String decision; // APPROVE, REJECT, FULFILL

    private String notes;

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

