package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.dto.PharmacyRequest;
import com.example.codebasebackend.dto.PharmacyResponse;
import com.example.codebasebackend.dto.PrescriptionRefillRequestPayload;
import com.example.codebasebackend.dto.PrescriptionRefillResponse;
import com.example.codebasebackend.dto.PrescriptionRequest;
import com.example.codebasebackend.dto.PrescriptionResponse;
import com.example.codebasebackend.dto.RefillDecisionRequest;
import com.example.codebasebackend.services.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "Prescription", entityIdExpression = "#result.body.id", includeArgs = true)
    public ResponseEntity<PrescriptionResponse> create(@Valid @RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.create(request));
    }

    @GetMapping("/{id}")
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Prescription", entityIdExpression = "#id", includeArgs = true)
    public ResponseEntity<PrescriptionResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.get(id));
    }

    @PutMapping("/{id}")
    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "Prescription", entityIdExpression = "#id", includeArgs = true)
    public ResponseEntity<PrescriptionResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Auditable(eventType = AuditLog.EventType.DELETE, entityType = "Prescription", entityIdExpression = "#id", includeArgs = true)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prescriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Prescription", includeArgs = true)
    public ResponseEntity<Page<PrescriptionResponse>> search(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(prescriptionService.search(patientId, status, searchTerm, page, size));
    }

    @GetMapping("/patient/{patientId}")
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Prescription", includeArgs = true)
    public ResponseEntity<List<PrescriptionResponse>> listByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(prescriptionService.listByPatientAndStatus(patientId, status));
    }

    @PatchMapping("/{id}/complete")
    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "Prescription", entityIdExpression = "#id", includeArgs = true)
    public ResponseEntity<PrescriptionResponse> markComplete(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.markCompleted(id));
    }

    @PatchMapping("/{id}/expire")
    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "Prescription", entityIdExpression = "#id", includeArgs = true)
    public ResponseEntity<PrescriptionResponse> markExpired(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.markExpired(id));
    }

    @GetMapping("/{id}/refills")
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "PrescriptionRefill", includeArgs = true)
    public ResponseEntity<List<PrescriptionRefillResponse>> listRefills(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.listRefills(id));
    }

    @PostMapping("/{id}/refills")
    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "PrescriptionRefill", includeArgs = true)
    public ResponseEntity<PrescriptionRefillResponse> requestRefill(@PathVariable Long id,
                                                                    @Valid @RequestBody PrescriptionRefillRequestPayload payload) {
        payload.setPrescriptionId(id);
        return ResponseEntity.ok(prescriptionService.requestRefill(payload));
    }

    @PatchMapping("/refills/{refillId}")
    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "PrescriptionRefill", entityIdExpression = "#refillId", includeArgs = true)
    public ResponseEntity<PrescriptionRefillResponse> decideRefill(@PathVariable Long refillId,
                                                                    @Valid @RequestBody RefillDecisionRequest decisionRequest) {
        return ResponseEntity.ok(prescriptionService.decideRefill(refillId, decisionRequest));
    }

    @GetMapping("/pharmacies")
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Pharmacy", includeArgs = true)
    public ResponseEntity<List<PharmacyResponse>> pharmacies() {
        return ResponseEntity.ok(prescriptionService.listPharmacies());
    }

    @PostMapping("/pharmacies")
    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "Pharmacy", entityIdExpression = "#result.body.id", includeArgs = true)
    public ResponseEntity<PharmacyResponse> savePharmacy(@Valid @RequestBody PharmacyRequest request) {
        return ResponseEntity.ok(prescriptionService.savePharmacy(request));
    }
}

