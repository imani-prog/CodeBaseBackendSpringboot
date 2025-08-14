package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.InsuranceClaim;
import com.example.codebasebackend.dto.InsuranceClaimRequest;
import com.example.codebasebackend.dto.InsuranceClaimResponse;
import com.example.codebasebackend.services.InsuranceClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class InsuranceClaimController {

    private final InsuranceClaimService claimService;

    @PostMapping
    public ResponseEntity<InsuranceClaimResponse> create(@Valid @RequestBody InsuranceClaimRequest request) {
        return ResponseEntity.ok(claimService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceClaimResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(claimService.get(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InsuranceClaimResponse> updateStatus(@PathVariable Long id,
                                                               @RequestParam String status,
                                                               @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(claimService.updateStatus(id, status, reason));
    }

    @GetMapping("/billing/{billingId}")
    public ResponseEntity<List<InsuranceClaimResponse>> listByBilling(@PathVariable Long billingId) {
        return ResponseEntity.ok(claimService.listByBilling(billingId));
    }

    @GetMapping("/provider/{providerId}/status/{status}")
    public ResponseEntity<List<InsuranceClaimResponse>> listByProviderAndStatus(@PathVariable Long providerId,
                                                                                @PathVariable String status) {
        return ResponseEntity.ok(claimService.listByProviderAndStatus(providerId, InsuranceClaim.ClaimStatus.valueOf(status.toUpperCase())));
    }
}

