package com.example.codebasebackend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public class AppointmentRequest {
    @NotNull
    private Long patientId;
    private Long hospitalId; // optional

    private String appointmentCode; // optional custom code

    @NotNull
    @FutureOrPresent
    private OffsetDateTime scheduledStart;

    @NotNull
    @Future
    private OffsetDateTime scheduledEnd;

    private String providerName;
    private String room;
    private String location;
    private String reason;
    private String notes;

    private String status; // enum name
    private String type;   // enum name
    private Boolean reminderSent;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public String getAppointmentCode() { return appointmentCode; }
    public void setAppointmentCode(String appointmentCode) { this.appointmentCode = appointmentCode; }
    public OffsetDateTime getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(OffsetDateTime scheduledStart) { this.scheduledStart = scheduledStart; }
    public OffsetDateTime getScheduledEnd() { return scheduledEnd; }
    public void setScheduledEnd(OffsetDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getReminderSent() { return reminderSent; }
    public void setReminderSent(Boolean reminderSent) { this.reminderSent = reminderSent; }
}

