package com.example.codebasebackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class ServiceOrderItemRequest {
    @NotBlank
    private String serviceName;
    private String serviceCode;
    @Min(1)
    private Integer quantity;
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal unitPrice;
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal lineTax; // optional per-line tax

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getLineTax() { return lineTax; }
    public void setLineTax(BigDecimal lineTax) { this.lineTax = lineTax; }
}

