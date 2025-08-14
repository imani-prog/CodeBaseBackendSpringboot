package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Billing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillingRepository extends JpaRepository<Billing, Long> {
    Optional<Billing> findByInvoiceNumber(String invoiceNumber);
    List<Billing> findByPatientIdOrderByIssueDateDesc(Long patientId);
    List<Billing> findByStatusOrderByIssueDateDesc(Billing.InvoiceStatus status);
}
