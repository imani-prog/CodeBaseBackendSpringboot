package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.InsurancePlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, Long> {
    List<InsurancePlan> findByProviderId(Long providerId);
    List<InsurancePlan> findByProviderIdAndStatusOrderByPlanNameAsc(Long providerId, InsurancePlan.PlanStatus status);
}

