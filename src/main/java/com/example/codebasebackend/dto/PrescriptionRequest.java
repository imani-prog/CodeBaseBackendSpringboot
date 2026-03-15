package com.example.codebasebackend.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class PrescriptionRequest {
    @NotNull
    private Long patientId;
    private Long doctorId;
    private Long hospitalId;
    private Long pharmacyId;

    @NotBlank
    private String medicationName;
    private String genericName;
    private String dosage;
    private String frequency;
    private String instructions;
    private String purpose;
    private String warnings;
    private List<String> sideEffects;

    @NotNull
    @PastOrPresent
    private LocalDate prescribedDate;

    @NotNull
    @PastOrPresent
    private LocalDate startDate;

    @FutureOrPresent
    private LocalDate endDate;

    @Min(0)
    private Integer totalRefills;

    @Min(0)
    private Integer refillsRemaining;

    @Min(0)
    @Max(100)
    private Integer progressPercent;

    private OffsetDateTime nextDoseAt;
    private Boolean reminderEnabled;
    private String status;
    private String providerSpecialty;

    private String prescriptionCode;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public Long getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(Long pharmacyId) { this.pharmacyId = pharmacyId; }
    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }
    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getWarnings() { return warnings; }
    public void setWarnings(String warnings) { this.warnings = warnings; }
    public List<String> getSideEffects() { return sideEffects; }
    public void setSideEffects(List<String> sideEffects) { this.sideEffects = sideEffects; }
    public LocalDate getPrescribedDate() { return prescribedDate; }
    public void setPrescribedDate(LocalDate prescribedDate) { this.prescribedDate = prescribedDate; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getTotalRefills() { return totalRefills; }
    public void setTotalRefills(Integer totalRefills) { this.totalRefills = totalRefills; }
    public Integer getRefillsRemaining() { return refillsRemaining; }
    public void setRefillsRemaining(Integer refillsRemaining) { this.refillsRemaining = refillsRemaining; }
    public Integer getProgressPercent() { return progressPercent; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
    public OffsetDateTime getNextDoseAt() { return nextDoseAt; }
    public void setNextDoseAt(OffsetDateTime nextDoseAt) { this.nextDoseAt = nextDoseAt; }
    public Boolean getReminderEnabled() { return reminderEnabled; }
    public void setReminderEnabled(Boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProviderSpecialty() { return providerSpecialty; }
    public void setProviderSpecialty(String providerSpecialty) { this.providerSpecialty = providerSpecialty; }
    public String getPrescriptionCode() { return prescriptionCode; }
    public void setPrescriptionCode(String prescriptionCode) { this.prescriptionCode = prescriptionCode; }
}

