package com.example.codebasebackend.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class InsuranceClaimResponse {
    private Long id;
    private Long billingId;
    private Long providerId;
    private Long policyId;
    private Long planId;
    private String claimNumber;
    private String status;
    private OffsetDateTime submissionDate;
    private OffsetDateTime responseDate;
    private BigDecimal claimedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal patientResponsibility;
    private String rejectionReason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBillingId() { return billingId; }
    public void setBillingId(Long billingId) { this.billingId = billingId; }
    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }
    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(OffsetDateTime submissionDate) { this.submissionDate = submissionDate; }
    public OffsetDateTime getResponseDate() { return responseDate; }
    public void setResponseDate(OffsetDateTime responseDate) { this.responseDate = responseDate; }
    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public void setClaimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    public BigDecimal getPatientResponsibility() { return patientResponsibility; }
    public void setPatientResponsibility(BigDecimal patientResponsibility) { this.patientResponsibility = patientResponsibility; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}

