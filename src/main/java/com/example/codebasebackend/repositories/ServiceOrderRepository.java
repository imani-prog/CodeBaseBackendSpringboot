package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    Optional<ServiceOrder> findByBillingId(Long billingId);
}
