package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.BillingRequest;
import com.example.codebasebackend.dto.BillingResponse;
import com.example.codebasebackend.Entities.Billing;

import java.util.List;

public interface BillingService {
    BillingResponse create(BillingRequest request);
    BillingResponse get(Long id);
    BillingResponse getByInvoice(String invoiceNumber);
    BillingResponse update(Long id, BillingRequest request);
    void delete(Long id);

    List<BillingResponse> listByPatient(Long patientId);
    List<BillingResponse> listByStatus(Billing.InvoiceStatus status);
}
