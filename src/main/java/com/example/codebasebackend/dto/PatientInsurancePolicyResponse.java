package com.example.codebasebackend.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class PatientInsurancePolicyResponse {
    private Long id;
    private Long patientId;
    private Long providerId;
    private Long planId;

    private String memberId;
    private String groupNumber;
    private String coverageLevel;
    private String policyholderName;
    private String policyholderRelation;
    private LocalDate policyholderDob;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String status;

    private String cardFrontUrl;
    private String cardBackUrl;
    private String notes;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getGroupNumber() { return groupNumber; }
    public void setGroupNumber(String groupNumber) { this.groupNumber = groupNumber; }
    public String getCoverageLevel() { return coverageLevel; }
    public void setCoverageLevel(String coverageLevel) { this.coverageLevel = coverageLevel; }
    public String getPolicyholderName() { return policyholderName; }
    public void setPolicyholderName(String policyholderName) { this.policyholderName = policyholderName; }
    public String getPolicyholderRelation() { return policyholderRelation; }
    public void setPolicyholderRelation(String policyholderRelation) { this.policyholderRelation = policyholderRelation; }
    public LocalDate getPolicyholderDob() { return policyholderDob; }
    public void setPolicyholderDob(LocalDate policyholderDob) { this.policyholderDob = policyholderDob; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public LocalDate getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCardFrontUrl() { return cardFrontUrl; }
    public void setCardFrontUrl(String cardFrontUrl) { this.cardFrontUrl = cardFrontUrl; }
    public String getCardBackUrl() { return cardBackUrl; }
    public void setCardBackUrl(String cardBackUrl) { this.cardBackUrl = cardBackUrl; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}

