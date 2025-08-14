package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.InsuranceClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {
    Optional<InsuranceClaim> findByClaimNumber(String claimNumber);
    List<InsuranceClaim> findByBillingId(Long billingId);
    List<InsuranceClaim> findByProviderIdAndStatus(Long providerId, InsuranceClaim.ClaimStatus status);
}

