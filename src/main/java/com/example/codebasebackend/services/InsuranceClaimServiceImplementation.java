package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.*;
import com.example.codebasebackend.dto.InsuranceClaimRequest;
import com.example.codebasebackend.dto.InsuranceClaimResponse;
import com.example.codebasebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class InsuranceClaimServiceImplementation implements InsuranceClaimService {

    private final InsuranceClaimRepository claimRepo;
    private final BillingRepository billingRepo;
    private final InsuranceProviderRepository providerRepo;
    private final PatientInsurancePolicyRepository policyRepo;
    private final InsurancePlanRepository planRepo;

    @Override
    public InsuranceClaimResponse create(InsuranceClaimRequest request) {
        Billing billing = billingRepo.findById(request.getBillingId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Invoice not found"));
        InsuranceProvider provider = providerRepo.findById(request.getProviderId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provider not found"));
        PatientInsurancePolicy policy = null;
        if (request.getPolicyId() != null) {
            policy = policyRepo.findById(request.getPolicyId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Policy not found"));
        }
        InsurancePlan plan = null;
        if (request.getPlanId() != null) {
            plan = planRepo.findById(request.getPlanId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Plan not found"));
        }
        InsuranceClaim claim = new InsuranceClaim();
        claim.setBilling(billing);
        claim.setProvider(provider);
        claim.setPolicy(policy);
        claim.setPlan(plan);
        claim.setClaimNumber(request.getClaimNumber());
        claim.setStatus(parseStatus(request.getStatus()));
        claim.setSubmissionDate(OffsetDateTime.now());
        claim.setClaimedAmount(request.getClaimedAmount());
        claim.setRejectionReason(request.getRejectionReason());
        InsuranceClaim saved = claimRepo.save(claim);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InsuranceClaimResponse get(Long id) {
        return claimRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Claim not found"));
    }

    @Override
    public InsuranceClaimResponse updateStatus(Long id, String status, String rejectionReason) {
        InsuranceClaim claim = claimRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Claim not found"));
        claim.setStatus(parseStatus(status));
        claim.setResponseDate(OffsetDateTime.now());
        claim.setRejectionReason(rejectionReason);
        InsuranceClaim saved = claimRepo.save(claim);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsuranceClaimResponse> listByBilling(Long billingId) {
        return claimRepo.findByBillingId(billingId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsuranceClaimResponse> listByProviderAndStatus(Long providerId, InsuranceClaim.ClaimStatus status) {
        return claimRepo.findByProviderIdAndStatus(providerId, status).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private InsuranceClaim.ClaimStatus parseStatus(String s) {
        try { return InsuranceClaim.ClaimStatus.valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid claim status"); }
    }

    private InsuranceClaimResponse toResponse(InsuranceClaim c) {
        InsuranceClaimResponse dto = new InsuranceClaimResponse();
        dto.setId(c.getId());
        dto.setBillingId(c.getBilling() != null ? c.getBilling().getId() : null);
        dto.setProviderId(c.getProvider() != null ? c.getProvider().getId() : null);
        dto.setPolicyId(c.getPolicy() != null ? c.getPolicy().getId() : null);
        dto.setPlanId(c.getPlan() != null ? c.getPlan().getId() : null);
        dto.setClaimNumber(c.getClaimNumber());
        dto.setStatus(c.getStatus() != null ? c.getStatus().name() : null);
        dto.setSubmissionDate(c.getSubmissionDate());
        dto.setResponseDate(c.getResponseDate());
        dto.setClaimedAmount(c.getClaimedAmount());
        dto.setApprovedAmount(c.getApprovedAmount());
        dto.setPatientResponsibility(c.getPatientResponsibility());
        dto.setRejectionReason(c.getRejectionReason());
        return dto;
    }
}

