package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.InsurancePlan;
import com.example.codebasebackend.Entities.InsuranceProvider;
import com.example.codebasebackend.Entities.PatientInsurancePolicy;
import com.example.codebasebackend.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface InsuranceService {
    // Providers
    InsuranceProviderResponse createProvider(InsuranceProviderRequest request);
    InsuranceProviderResponse getProvider(Long id);
    InsuranceProviderResponse updateProvider(Long id, InsuranceProviderRequest request);
    void deleteProvider(Long id);
    List<InsuranceProviderResponse> listProvidersByStatus(InsuranceProvider.ProviderStatus status);
    List<InsuranceProviderResponse> searchProvidersByName(String q);

    // Plans
    InsurancePlanResponse createPlan(InsurancePlanRequest request);
    InsurancePlanResponse getPlan(Long id);
    InsurancePlanResponse updatePlan(Long id, InsurancePlanRequest request);
    void deletePlan(Long id);
    List<InsurancePlanResponse> listPlansByProvider(Long providerId);
    List<InsurancePlanResponse> listPlansByProviderAndStatus(Long providerId, InsurancePlan.PlanStatus status);

    // Patient policies
    PatientInsurancePolicyResponse createPolicy(PatientInsurancePolicyRequest request);
    PatientInsurancePolicyResponse getPolicy(Long id);
    PatientInsurancePolicyResponse updatePolicy(Long id, PatientInsurancePolicyRequest request);
    void deletePolicy(Long id);
    List<PatientInsurancePolicyResponse> listPoliciesByPatient(Long patientId);
    List<PatientInsurancePolicyResponse> listPoliciesByProvider(Long providerId);
    List<PatientInsurancePolicyResponse> listActivePoliciesForPatientInRange(Long patientId, LocalDate from, LocalDate to);
}
