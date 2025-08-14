package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.InsuranceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InsuranceProviderRepository extends JpaRepository<InsuranceProvider, Long> {
    Optional<InsuranceProvider> findByNameIgnoreCase(String name);
    Optional<InsuranceProvider> findByPayerId(String payerId);
    List<InsuranceProvider> findByStatus(InsuranceProvider.ProviderStatus status);
    List<InsuranceProvider> findByNameContainingIgnoreCase(String partial);
}
