package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.IntegrationPartner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntegrationPartnerRepository extends JpaRepository<IntegrationPartner, Long> {
    Optional<IntegrationPartner> findByNameIgnoreCase(String name);
    List<IntegrationPartner> findByType(IntegrationPartner.PartnerType type);
    List<IntegrationPartner> findByStatus(IntegrationPartner.PartnerStatus status);
    List<IntegrationPartner> findByNameContainingIgnoreCase(String q);
}
