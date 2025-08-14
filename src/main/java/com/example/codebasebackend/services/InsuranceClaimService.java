package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.InsuranceClaimRequest;
import com.example.codebasebackend.dto.InsuranceClaimResponse;
import com.example.codebasebackend.Entities.InsuranceClaim;

import java.util.List;

public interface InsuranceClaimService {
    InsuranceClaimResponse create(InsuranceClaimRequest request);
    InsuranceClaimResponse get(Long id);
    InsuranceClaimResponse updateStatus(Long id, String status, String rejectionReason);
    List<InsuranceClaimResponse> listByBilling(Long billingId);
    List<InsuranceClaimResponse> listByProviderAndStatus(Long providerId, InsuranceClaim.ClaimStatus status);
}

