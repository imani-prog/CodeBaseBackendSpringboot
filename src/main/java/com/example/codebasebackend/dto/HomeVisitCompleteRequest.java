package com.example.codebasebackend.dto;

public class HomeVisitCompleteRequest {
    private String outcome;
    private String notes;

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

