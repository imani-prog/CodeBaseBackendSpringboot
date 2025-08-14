package com.example.codebasebackend.controllers;

import com.example.codebasebackend.dto.PatientInsurancePolicyRequest;
import com.example.codebasebackend.dto.PatientInsurancePolicyResponse;
import com.example.codebasebackend.services.InsuranceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/insurance/policies")
@RequiredArgsConstructor
public class PatientInsurancePolicyController {

    private final InsuranceService insuranceService;

    @PostMapping
    public ResponseEntity<PatientInsurancePolicyResponse> create(@Valid @RequestBody PatientInsurancePolicyRequest request) {
        return ResponseEntity.ok(insuranceService.createPolicy(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientInsurancePolicyResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(insuranceService.getPolicy(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientInsurancePolicyResponse> update(@PathVariable Long id,
                                                                 @Valid @RequestBody PatientInsurancePolicyRequest request) {
        return ResponseEntity.ok(insuranceService.updatePolicy(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        insuranceService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientInsurancePolicyResponse>> byPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(insuranceService.listPoliciesByPatient(patientId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<PatientInsurancePolicyResponse>> byProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(insuranceService.listPoliciesByProvider(providerId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<PatientInsurancePolicyResponse>> activeInRange(
            @RequestParam Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(insuranceService.listActivePoliciesForPatientInRange(patientId, from, to));
    }
}

