package com.example.codebasebackend.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class RemittanceAdviceResponse {
    private Long id;
    private Long claimId;
    private BigDecimal amountPaid;
    private String payerReference;
    private OffsetDateTime remittanceDate;
    private String adjustments;
    private String notes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    public String getPayerReference() { return payerReference; }
    public void setPayerReference(String payerReference) { this.payerReference = payerReference; }
    public OffsetDateTime getRemittanceDate() { return remittanceDate; }
    public void setRemittanceDate(OffsetDateTime remittanceDate) { this.remittanceDate = remittanceDate; }
    public String getAdjustments() { return adjustments; }
    public void setAdjustments(String adjustments) { this.adjustments = adjustments; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}

