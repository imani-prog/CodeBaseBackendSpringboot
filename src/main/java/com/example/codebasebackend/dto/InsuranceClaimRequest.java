package com.example.codebasebackend.dto;

import java.math.BigDecimal;

public class InsuranceClaimRequest {
    private Long billingId;
    private Long providerId;
    private Long policyId; // optional
    private Long planId;   // optional
    private String claimNumber; // optional
    private String status; // enum name
    private BigDecimal claimedAmount;
    private String rejectionReason;

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
    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public void setClaimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}

