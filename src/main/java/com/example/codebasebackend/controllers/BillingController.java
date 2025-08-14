package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.Billing;
import com.example.codebasebackend.dto.BillingRequest;
import com.example.codebasebackend.dto.BillingResponse;
import com.example.codebasebackend.services.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping
    public ResponseEntity<BillingResponse> create(@Valid @RequestBody BillingRequest request) {
        return ResponseEntity.ok(billingService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.get(id));
    }

    @GetMapping("/invoice/{invoiceNumber}")
    public ResponseEntity<BillingResponse> getByInvoice(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(billingService.getByInvoice(invoiceNumber));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillingResponse> update(@PathVariable Long id, @Valid @RequestBody BillingRequest request) {
        return ResponseEntity.ok(billingService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        billingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BillingResponse>> listByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(billingService.listByPatient(patientId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BillingResponse>> listByStatus(@PathVariable String status) {
        return ResponseEntity.ok(billingService.listByStatus(Billing.InvoiceStatus.valueOf(status.toUpperCase())));
    }
}
