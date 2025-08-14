package com.example.codebasebackend.controllers;

import com.example.codebasebackend.dto.PaymentRequest;
import com.example.codebasebackend.dto.PaymentResponse;
import com.example.codebasebackend.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> record(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.record(request));
    }

    @GetMapping("/billing/{billingId}")
    public ResponseEntity<List<PaymentResponse>> listByBilling(@PathVariable Long billingId) {
        return ResponseEntity.ok(paymentService.listByBilling(billingId));
    }
}

