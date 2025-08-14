package com.example.codebasebackend.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class InsurancePlanResponse {
    private Long id;
    private Long providerId;
    private String planName;
    private String planCode;
    private String planType;
    private String networkType;
    private String coverageDetails;
    private BigDecimal deductibleIndividual;
    private BigDecimal deductibleFamily;
    private BigDecimal oopMaxIndividual;
    private BigDecimal oopMaxFamily;
    private BigDecimal copayPrimaryCare;
    private BigDecimal copaySpecialist;
    private BigDecimal copayEmergency;
    private Integer coinsurancePercent;
    private Boolean requiresReferral;
    private Boolean preauthRequired;
    private OffsetDateTime effectiveFrom;
    private OffsetDateTime effectiveTo;
    private String status;
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }
    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }
    public String getNetworkType() { return networkType; }
    public void setNetworkType(String networkType) { this.networkType = networkType; }
    public String getCoverageDetails() { return coverageDetails; }
    public void setCoverageDetails(String coverageDetails) { this.coverageDetails = coverageDetails; }
    public BigDecimal getDeductibleIndividual() { return deductibleIndividual; }
    public void setDeductibleIndividual(BigDecimal deductibleIndividual) { this.deductibleIndividual = deductibleIndividual; }
    public BigDecimal getDeductibleFamily() { return deductibleFamily; }
    public void setDeductibleFamily(BigDecimal deductibleFamily) { this.deductibleFamily = deductibleFamily; }
    public BigDecimal getOopMaxIndividual() { return oopMaxIndividual; }
    public void setOopMaxIndividual(BigDecimal oopMaxIndividual) { this.oopMaxIndividual = oopMaxIndividual; }
    public BigDecimal getOopMaxFamily() { return oopMaxFamily; }
    public void setOopMaxFamily(BigDecimal oopMaxFamily) { this.oopMaxFamily = oopMaxFamily; }
    public BigDecimal getCopayPrimaryCare() { return copayPrimaryCare; }
    public void setCopayPrimaryCare(BigDecimal copayPrimaryCare) { this.copayPrimaryCare = copayPrimaryCare; }
    public BigDecimal getCopaySpecialist() { return copaySpecialist; }
    public void setCopaySpecialist(BigDecimal copaySpecialist) { this.copaySpecialist = copaySpecialist; }
    public BigDecimal getCopayEmergency() { return copayEmergency; }
    public void setCopayEmergency(BigDecimal copayEmergency) { this.copayEmergency = copayEmergency; }
    public Integer getCoinsurancePercent() { return coinsurancePercent; }
    public void setCoinsurancePercent(Integer coinsurancePercent) { this.coinsurancePercent = coinsurancePercent; }
    public Boolean getRequiresReferral() { return requiresReferral; }
    public void setRequiresReferral(Boolean requiresReferral) { this.requiresReferral = requiresReferral; }
    public Boolean getPreauthRequired() { return preauthRequired; }
    public void setPreauthRequired(Boolean preauthRequired) { this.preauthRequired = preauthRequired; }
    public OffsetDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(OffsetDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public OffsetDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(OffsetDateTime effectiveTo) { this.effectiveTo = effectiveTo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

