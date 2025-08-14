package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.InsurancePlan;
import com.example.codebasebackend.Entities.InsuranceProvider;
import com.example.codebasebackend.Entities.PatientInsurancePolicy;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.dto.*;
import com.example.codebasebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class InsuranceServiceImplementation implements InsuranceService {

    private final InsuranceProviderRepository providerRepo;
    private final InsurancePlanRepository planRepo;
    private final PatientInsurancePolicyRepository policyRepo;
    private final PatientRepository patientRepo;

    // Providers
    @Override
    public InsuranceProviderResponse createProvider(InsuranceProviderRequest request) {
        InsuranceProvider entity = new InsuranceProvider();
        applyProvider(entity, request);
        InsuranceProvider saved = providerRepo.save(entity);
        return toProviderResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InsuranceProviderResponse getProvider(Long id) {
        InsuranceProvider entity = providerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provider not found"));
        return toProviderResponse(entity);
    }

    @Override
    public InsuranceProviderResponse updateProvider(Long id, InsuranceProviderRequest request) {
        InsuranceProvider entity = providerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provider not found"));
        applyProvider(entity, request);
        InsuranceProvider saved = providerRepo.save(entity);
        return toProviderResponse(saved);
    }

    @Override
    public void deleteProvider(Long id) {
        if (!providerRepo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Provider not found");
        providerRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsuranceProviderResponse> listProvidersByStatus(InsuranceProvider.ProviderStatus status) {
        return providerRepo.findByStatus(status).stream().map(this::toProviderResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsuranceProviderResponse> searchProvidersByName(String q) {
        return providerRepo.findByNameContainingIgnoreCase(q == null ? "" : q)
                .stream().map(this::toProviderResponse).collect(Collectors.toList());
    }

    // Plans
    @Override
    public InsurancePlanResponse createPlan(InsurancePlanRequest request) {
        InsuranceProvider provider = providerRepo.findById(request.getProviderId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provider not found"));
        InsurancePlan plan = new InsurancePlan();
        plan.setProvider(provider);
        applyPlan(plan, request);
        InsurancePlan saved = planRepo.save(plan);
        return toPlanResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InsurancePlanResponse getPlan(Long id) {
        InsurancePlan plan = planRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Plan not found"));
        return toPlanResponse(plan);
    }

    @Override
    public InsurancePlanResponse updatePlan(Long id, InsurancePlanRequest request) {
        InsurancePlan plan = planRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Plan not found"));
        if (request.getProviderId() != null && (plan.getProvider() == null || !plan.getProvider().getId().equals(request.getProviderId()))) {
            InsuranceProvider provider = providerRepo.findById(request.getProviderId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provider not found"));
            plan.setProvider(provider);
        }
        applyPlan(plan, request);
        InsurancePlan saved = planRepo.save(plan);
        return toPlanResponse(saved);
    }

    @Override
    public void deletePlan(Long id) {
        if (!planRepo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Plan not found");
        planRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsurancePlanResponse> listPlansByProvider(Long providerId) {
        return planRepo.findByProviderId(providerId).stream().map(this::toPlanResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsurancePlanResponse> listPlansByProviderAndStatus(Long providerId, InsurancePlan.PlanStatus status) {
        return planRepo.findByProviderIdAndStatusOrderByPlanNameAsc(providerId, status)
                .stream().map(this::toPlanResponse).collect(Collectors.toList());
    }

    // Policies
    @Override
    public PatientInsurancePolicyResponse createPolicy(PatientInsurancePolicyRequest request) {
        Patient patient = patientRepo.findById(request.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
        InsuranceProvider provider = providerRepo.findById(request.getProviderId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provider not found"));
        InsurancePlan plan = null;
        if (request.getPlanId() != null) {
            plan = planRepo.findById(request.getPlanId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Plan not found"));
        }
        PatientInsurancePolicy policy = new PatientInsurancePolicy();
        policy.setPatient(patient);
        policy.setProvider(provider);
        policy.setPlan(plan);
        applyPolicy(policy, request);
        PatientInsurancePolicy saved = policyRepo.save(policy);
        return toPolicyResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientInsurancePolicyResponse getPolicy(Long id) {
        return policyRepo.findById(id).map(this::toPolicyResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Policy not found"));
    }

    @Override
    public PatientInsurancePolicyResponse updatePolicy(Long id, PatientInsurancePolicyRequest request) {
        PatientInsurancePolicy policy = policyRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Policy not found"));
        if (request.getPatientId() != null && !policy.getPatient().getId().equals(request.getPatientId())) {
            Patient patient = patientRepo.findById(request.getPatientId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
            policy.setPatient(patient);
        }
        if (request.getProviderId() != null && !policy.getProvider().getId().equals(request.getProviderId())) {
            InsuranceProvider provider = providerRepo.findById(request.getProviderId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provider not found"));
            policy.setProvider(provider);
        }
        if (request.getPlanId() != null) {
            InsurancePlan plan = planRepo.findById(request.getPlanId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Plan not found"));
            policy.setPlan(plan);
        } else {
            policy.setPlan(null);
        }
        applyPolicy(policy, request);
        PatientInsurancePolicy saved = policyRepo.save(policy);
        return toPolicyResponse(saved);
    }

    @Override
    public void deletePolicy(Long id) {
        if (!policyRepo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Policy not found");
        policyRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientInsurancePolicyResponse> listPoliciesByPatient(Long patientId) {
        return policyRepo.findByPatientId(patientId).stream().map(this::toPolicyResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientInsurancePolicyResponse> listPoliciesByProvider(Long providerId) {
        return policyRepo.findByProviderId(providerId).stream().map(this::toPolicyResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientInsurancePolicyResponse> listActivePoliciesForPatientInRange(Long patientId, LocalDate from, LocalDate to) {
        if (from == null || to == null || !to.isAfter(from)) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid range");
        }
        // Fetch all for patient and filter with inclusive overlap, treating null effectiveTo as open-ended
        return policyRepo.findByPatientId(patientId).stream()
                .filter(p -> p.getEffectiveFrom() != null && !p.getEffectiveFrom().isAfter(to))
                .filter(p -> p.getEffectiveTo() == null || !p.getEffectiveTo().isBefore(from))
                .map(this::toPolicyResponse).collect(Collectors.toList());
    }

    // Mapping helpers
    private void applyProvider(InsuranceProvider e, InsuranceProviderRequest r) {
        e.setName(r.getName());
        e.setPayerId(r.getPayerId());
        e.setRegistrationNumber(r.getRegistrationNumber());
        e.setEmail(r.getEmail());
        e.setPhone(r.getPhone());
        e.setFax(r.getFax());
        e.setWebsite(r.getWebsite());
        e.setProviderPortalUrl(r.getProviderPortalUrl());
        e.setClaimsSubmissionUrl(r.getClaimsSubmissionUrl());
        e.setClaimsSubmissionEmail(r.getClaimsSubmissionEmail());
        e.setSupportPhone(r.getSupportPhone());
        e.setSupportEmail(r.getSupportEmail());
        e.setAddressLine1(r.getAddressLine1());
        e.setAddressLine2(r.getAddressLine2());
        e.setCity(r.getCity());
        e.setState(r.getState());
        e.setPostalCode(r.getPostalCode());
        e.setCountry(r.getCountry());
        if (r.getStatus() != null) {
            try { e.setStatus(InsuranceProvider.ProviderStatus.valueOf(r.getStatus().toUpperCase())); }
            catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid provider status"); }
        } else if (e.getStatus() == null) {
            e.setStatus(InsuranceProvider.ProviderStatus.ACTIVE);
        }
        e.setNotes(r.getNotes());
    }

    private InsuranceProviderResponse toProviderResponse(InsuranceProvider e) {
        InsuranceProviderResponse dto = new InsuranceProviderResponse();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setPayerId(e.getPayerId());
        dto.setRegistrationNumber(e.getRegistrationNumber());
        dto.setEmail(e.getEmail());
        dto.setPhone(e.getPhone());
        dto.setFax(e.getFax());
        dto.setWebsite(e.getWebsite());
        dto.setProviderPortalUrl(e.getProviderPortalUrl());
        dto.setClaimsSubmissionUrl(e.getClaimsSubmissionUrl());
        dto.setClaimsSubmissionEmail(e.getClaimsSubmissionEmail());
        dto.setSupportPhone(e.getSupportPhone());
        dto.setSupportEmail(e.getSupportEmail());
        dto.setAddressLine1(e.getAddressLine1());
        dto.setAddressLine2(e.getAddressLine2());
        dto.setCity(e.getCity());
        dto.setState(e.getState());
        dto.setPostalCode(e.getPostalCode());
        dto.setCountry(e.getCountry());
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setNotes(e.getNotes());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    private void applyPlan(InsurancePlan e, InsurancePlanRequest r) {
        e.setPlanName(r.getPlanName());
        e.setPlanCode(r.getPlanCode());
        if (r.getPlanType() != null) {
            try { e.setPlanType(InsurancePlan.PlanType.valueOf(r.getPlanType().toUpperCase())); }
            catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid planType"); }
        } else if (e.getPlanType() == null) {
            e.setPlanType(InsurancePlan.PlanType.OTHER);
        }
        if (r.getNetworkType() != null) {
            try { e.setNetworkType(InsurancePlan.NetworkType.valueOf(r.getNetworkType().toUpperCase())); }
            catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid networkType"); }
        } else if (e.getNetworkType() == null) {
            e.setNetworkType(InsurancePlan.NetworkType.BOTH);
        }
        e.setCoverageDetails(r.getCoverageDetails());
        e.setDeductibleIndividual(r.getDeductibleIndividual());
        e.setDeductibleFamily(r.getDeductibleFamily());
        e.setOopMaxIndividual(r.getOopMaxIndividual());
        e.setOopMaxFamily(r.getOopMaxFamily());
        e.setCopayPrimaryCare(r.getCopayPrimaryCare());
        e.setCopaySpecialist(r.getCopaySpecialist());
        e.setCopayEmergency(r.getCopayEmergency());
        e.setCoinsurancePercent(r.getCoinsurancePercent());
        e.setRequiresReferral(r.getRequiresReferral());
        e.setPreauthRequired(r.getPreauthRequired());
        e.setEffectiveFrom(r.getEffectiveFrom());
        e.setEffectiveTo(r.getEffectiveTo());
        if (r.getStatus() != null) {
            try { e.setStatus(InsurancePlan.PlanStatus.valueOf(r.getStatus().toUpperCase())); }
            catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid plan status"); }
        } else if (e.getStatus() == null) {
            e.setStatus(InsurancePlan.PlanStatus.ACTIVE);
        }
        e.setNotes(r.getNotes());
    }

    private InsurancePlanResponse toPlanResponse(InsurancePlan e) {
        InsurancePlanResponse dto = new InsurancePlanResponse();
        dto.setId(e.getId());
        dto.setProviderId(e.getProvider() != null ? e.getProvider().getId() : null);
        dto.setPlanName(e.getPlanName());
        dto.setPlanCode(e.getPlanCode());
        dto.setPlanType(e.getPlanType() != null ? e.getPlanType().name() : null);
        dto.setNetworkType(e.getNetworkType() != null ? e.getNetworkType().name() : null);
        dto.setCoverageDetails(e.getCoverageDetails());
        dto.setDeductibleIndividual(e.getDeductibleIndividual());
        dto.setDeductibleFamily(e.getDeductibleFamily());
        dto.setOopMaxIndividual(e.getOopMaxIndividual());
        dto.setOopMaxFamily(e.getOopMaxFamily());
        dto.setCopayPrimaryCare(e.getCopayPrimaryCare());
        dto.setCopaySpecialist(e.getCopaySpecialist());
        dto.setCopayEmergency(e.getCopayEmergency());
        dto.setCoinsurancePercent(e.getCoinsurancePercent());
        dto.setRequiresReferral(e.getRequiresReferral());
        dto.setPreauthRequired(e.getPreauthRequired());
        dto.setEffectiveFrom(e.getEffectiveFrom());
        dto.setEffectiveTo(e.getEffectiveTo());
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setNotes(e.getNotes());
        return dto;
    }

    private void applyPolicy(PatientInsurancePolicy e, PatientInsurancePolicyRequest r) {
        e.setMemberId(r.getMemberId());
        e.setGroupNumber(r.getGroupNumber());
        if (r.getCoverageLevel() != null) {
            try { e.setCoverageLevel(PatientInsurancePolicy.CoverageLevel.valueOf(r.getCoverageLevel().toUpperCase())); }
            catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid coverageLevel"); }
        } else if (e.getCoverageLevel() == null) {
            e.setCoverageLevel(PatientInsurancePolicy.CoverageLevel.PRIMARY);
        }
        e.setPolicyholderName(r.getPolicyholderName());
        e.setPolicyholderRelation(r.getPolicyholderRelation());
        e.setPolicyholderDob(r.getPolicyholderDob());
        if (r.getEffectiveFrom() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "effectiveFrom is required");
        }
        e.setEffectiveFrom(r.getEffectiveFrom());
        e.setEffectiveTo(r.getEffectiveTo());
        if (r.getStatus() != null) {
            try { e.setStatus(PatientInsurancePolicy.PolicyStatus.valueOf(r.getStatus().toUpperCase())); }
            catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid policy status"); }
        } else if (e.getStatus() == null) {
            e.setStatus(PatientInsurancePolicy.PolicyStatus.ACTIVE);
        }
        e.setCardFrontUrl(r.getCardFrontUrl());
        e.setCardBackUrl(r.getCardBackUrl());
        e.setNotes(r.getNotes());
    }

    private PatientInsurancePolicyResponse toPolicyResponse(PatientInsurancePolicy e) {
        PatientInsurancePolicyResponse dto = new PatientInsurancePolicyResponse();
        dto.setId(e.getId());
        dto.setPatientId(e.getPatient() != null ? e.getPatient().getId() : null);
        dto.setProviderId(e.getProvider() != null ? e.getProvider().getId() : null);
        dto.setPlanId(e.getPlan() != null ? e.getPlan().getId() : null);
        dto.setMemberId(e.getMemberId());
        dto.setGroupNumber(e.getGroupNumber());
        dto.setCoverageLevel(e.getCoverageLevel() != null ? e.getCoverageLevel().name() : null);
        dto.setPolicyholderName(e.getPolicyholderName());
        dto.setPolicyholderRelation(e.getPolicyholderRelation());
        dto.setPolicyholderDob(e.getPolicyholderDob());
        dto.setEffectiveFrom(e.getEffectiveFrom());
        dto.setEffectiveTo(e.getEffectiveTo());
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setCardFrontUrl(e.getCardFrontUrl());
        dto.setCardBackUrl(e.getCardBackUrl());
        dto.setNotes(e.getNotes());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }
}
