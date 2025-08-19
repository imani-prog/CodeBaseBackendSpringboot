package com.example.codebasebackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class ServiceOrderRequest {
    @NotNull
    private Long patientId;
    private Long hospitalId; // optional
    private String currency; // optional; defaults to KES
    private String notes;
    private BigDecimal discount; // optional order-level discount
    private BigDecimal tax; // optional order-level tax (if omitted, sum of line taxes is used)

    @Valid
    @NotEmpty
    private List<ServiceOrderItemRequest> items;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }
    public List<ServiceOrderItemRequest> getItems() { return items; }
    public void setItems(List<ServiceOrderItemRequest> items) { this.items = items; }
}

