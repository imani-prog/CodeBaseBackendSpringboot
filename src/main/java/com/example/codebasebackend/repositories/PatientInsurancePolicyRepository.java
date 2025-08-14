package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.PatientInsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientInsurancePolicyRepository extends JpaRepository<PatientInsurancePolicy, Long> {
    List<PatientInsurancePolicy> findByPatientId(Long patientId);
    List<PatientInsurancePolicy> findByProviderId(Long providerId);
    List<PatientInsurancePolicy> findByPatientIdAndStatus(Long patientId, PatientInsurancePolicy.PolicyStatus status);
    Optional<PatientInsurancePolicy> findByProviderIdAndMemberId(Long providerId, String memberId);
    List<PatientInsurancePolicy> findByPatientIdAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(Long patientId, LocalDate from, LocalDate to);
}

