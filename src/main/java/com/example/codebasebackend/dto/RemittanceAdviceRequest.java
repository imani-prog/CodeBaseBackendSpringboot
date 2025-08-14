package com.example.codebasebackend.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class RemittanceAdviceRequest {
    private Long claimId;
    private BigDecimal amountPaid;
    private String payerReference;
    private OffsetDateTime remittanceDate;
    private String adjustments;
    private String notes;

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
}

