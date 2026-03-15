package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.PharmacyRequest;
import com.example.codebasebackend.dto.PharmacyResponse;
import com.example.codebasebackend.dto.PrescriptionRefillRequestPayload;
import com.example.codebasebackend.dto.PrescriptionRefillResponse;
import com.example.codebasebackend.dto.PrescriptionRequest;
import com.example.codebasebackend.dto.PrescriptionResponse;
import com.example.codebasebackend.dto.RefillDecisionRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PrescriptionService {
    PrescriptionResponse create(PrescriptionRequest request);
    PrescriptionResponse update(Long id, PrescriptionRequest request);
    PrescriptionResponse get(Long id);
    void delete(Long id);

    Page<PrescriptionResponse> search(Long patientId, String status, String searchTerm, int page, int size);
    List<PrescriptionResponse> listByPatientAndStatus(Long patientId, String status);

    PrescriptionResponse markCompleted(Long id);
    PrescriptionResponse markExpired(Long id);

    PrescriptionRefillResponse requestRefill(PrescriptionRefillRequestPayload request);
    PrescriptionRefillResponse decideRefill(Long refillRequestId, RefillDecisionRequest decisionRequest);
    List<PrescriptionRefillResponse> listRefills(Long prescriptionId);

    PharmacyResponse savePharmacy(PharmacyRequest request);
    List<PharmacyResponse> listPharmacies();
}

