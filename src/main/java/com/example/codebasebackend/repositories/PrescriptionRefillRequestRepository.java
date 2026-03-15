package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.PrescriptionRefillRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRefillRequestRepository extends JpaRepository<PrescriptionRefillRequest, Long> {
    List<PrescriptionRefillRequest> findByPrescriptionIdOrderByRequestedAtDesc(Long prescriptionId);

    Page<PrescriptionRefillRequest> findByStatus(PrescriptionRefillRequest.RefillStatus status, Pageable pageable);
}

