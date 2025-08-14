package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.InsuranceProvider;
import com.example.codebasebackend.dto.InsuranceProviderRequest;
import com.example.codebasebackend.dto.InsuranceProviderResponse;
import com.example.codebasebackend.services.InsuranceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurance/providers")
@RequiredArgsConstructor
public class InsuranceProviderController {

    private final InsuranceService insuranceService;

    @PostMapping
    public ResponseEntity<InsuranceProviderResponse> create(@Valid @RequestBody InsuranceProviderRequest request) {
        return ResponseEntity.ok(insuranceService.createProvider(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceProviderResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(insuranceService.getProvider(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsuranceProviderResponse> update(@PathVariable Long id,
                                                            @Valid @RequestBody InsuranceProviderRequest request) {
        return ResponseEntity.ok(insuranceService.updateProvider(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        insuranceService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<InsuranceProviderResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "q") String query) {
        if (status != null && !status.isBlank()) {
            InsuranceProvider.ProviderStatus st = InsuranceProvider.ProviderStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(insuranceService.listProvidersByStatus(st));
        }
        return ResponseEntity.ok(insuranceService.searchProvidersByName(query));
    }
}
