package com.example.codebasebackend.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class HealthRecordResponse {
    private Long id;
    private String recordCode;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long hospitalId;
    private String hospitalName;

    private String recordType;
    private String status;
    private LocalDate visitDate;
    private LocalDate dueDate;

    private String providerName;
    private String providerSpecialty;
    private String summary;
    private String notes;
    private String diagnosis;

    private String vaccineName;

    private String medicationName;
    private String dosage;
    private String frequency;
    private String durationText;
    private Integer refillsRemaining;
    private Integer totalRefills;

    private List<HealthRecordAttachmentDto> attachments;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRecordCode() { return recordCode; }
    public void setRecordCode(String recordCode) { this.recordCode = recordCode; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public String getProviderSpecialty() { return providerSpecialty; }
    public void setProviderSpecialty(String providerSpecialty) { this.providerSpecialty = providerSpecialty; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }
    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getDurationText() { return durationText; }
    public void setDurationText(String durationText) { this.durationText = durationText; }
    public Integer getRefillsRemaining() { return refillsRemaining; }
    public void setRefillsRemaining(Integer refillsRemaining) { this.refillsRemaining = refillsRemaining; }
    public Integer getTotalRefills() { return totalRefills; }
    public void setTotalRefills(Integer totalRefills) { this.totalRefills = totalRefills; }
    public List<HealthRecordAttachmentDto> getAttachments() { return attachments; }
    public void setAttachments(List<HealthRecordAttachmentDto> attachments) { this.attachments = attachments; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}

