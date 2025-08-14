package com.example.codebasebackend.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentResponse {
    private Long id;
    private Long billingId;
    private String method;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String externalReference;
    private String notes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBillingId() { return billingId; }
    public void setBillingId(Long billingId) { this.billingId = billingId; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}

