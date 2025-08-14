package com.example.codebasebackend.dto;

import java.time.OffsetDateTime;

public class AppointmentResponse {
    private Long id;
    private String appointmentCode;
    private Long patientId;
    private Long hospitalId;
    private OffsetDateTime scheduledStart;
    private OffsetDateTime scheduledEnd;
    private OffsetDateTime checkInTime;
    private OffsetDateTime checkOutTime;
    private String status;
    private String type;
    private String providerName;
    private String room;
    private String location;
    private String reason;
    private String notes;
    private Boolean reminderSent;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppointmentCode() { return appointmentCode; }
    public void setAppointmentCode(String appointmentCode) { this.appointmentCode = appointmentCode; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public OffsetDateTime getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(OffsetDateTime scheduledStart) { this.scheduledStart = scheduledStart; }
    public OffsetDateTime getScheduledEnd() { return scheduledEnd; }
    public void setScheduledEnd(OffsetDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }
    public OffsetDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(OffsetDateTime checkInTime) { this.checkInTime = checkInTime; }
    public OffsetDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(OffsetDateTime checkOutTime) { this.checkOutTime = checkOutTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
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
    public Boolean getReminderSent() { return reminderSent; }
    public void setReminderSent(Boolean reminderSent) { this.reminderSent = reminderSent; }
}

