package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotNull;

public class ChwAssignmentRequest {
    @NotNull
    private Long patientId;

    @NotNull
    private Long chwId;

    private String assignmentType;
    private String status;
    private Long appointmentId;
    private String location;
    private String notes;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getChwId() { return chwId; }
    public void setChwId(Long chwId) { this.chwId = chwId; }

    public String getAssignmentType() { return assignmentType; }
    public void setAssignmentType(String assignmentType) { this.assignmentType = assignmentType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

