package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.PaymentRequest;
import com.example.codebasebackend.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse record(PaymentRequest request);
    List<PaymentResponse> listByBilling(Long billingId);
}

