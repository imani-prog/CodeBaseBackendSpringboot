package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.InsurancePlan;
import com.example.codebasebackend.dto.InsurancePlanRequest;
import com.example.codebasebackend.dto.InsurancePlanResponse;
import com.example.codebasebackend.services.InsuranceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurance/plans")
@RequiredArgsConstructor
public class InsurancePlanController {

    private final InsuranceService insuranceService;

    @PostMapping
    public ResponseEntity<InsurancePlanResponse> create(@Valid @RequestBody InsurancePlanRequest request) {
        return ResponseEntity.ok(insuranceService.createPlan(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsurancePlanResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(insuranceService.getPlan(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsurancePlanResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody InsurancePlanRequest request) {
        return ResponseEntity.ok(insuranceService.updatePlan(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        insuranceService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<InsurancePlanResponse>> listByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(insuranceService.listPlansByProvider(providerId));
    }

    @GetMapping("/provider/{providerId}/status/{status}")
    public ResponseEntity<List<InsurancePlanResponse>> listByProviderAndStatus(@PathVariable Long providerId,
                                                                               @PathVariable String status) {
        InsurancePlan.PlanStatus st = InsurancePlan.PlanStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(insuranceService.listPlansByProviderAndStatus(providerId, st));
    }
}

