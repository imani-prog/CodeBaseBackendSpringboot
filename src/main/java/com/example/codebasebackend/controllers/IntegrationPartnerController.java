package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.IntegrationPartner;
import com.example.codebasebackend.dto.IntegrationPartnerRequest;
import com.example.codebasebackend.dto.IntegrationPartnerResponse;
import com.example.codebasebackend.services.IntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integration-partners")
@RequiredArgsConstructor
public class IntegrationPartnerController {

    private final IntegrationService service;

    @PostMapping
    public ResponseEntity<IntegrationPartnerResponse> create(@Valid @RequestBody IntegrationPartnerRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntegrationPartnerResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IntegrationPartnerResponse> update(@PathVariable Long id,
                                                             @Valid @RequestBody IntegrationPartnerRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<IntegrationPartnerResponse>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "q") String query) {
        if (type != null && !type.isBlank()) {
            return ResponseEntity.ok(service.listByType(IntegrationPartner.PartnerType.valueOf(type.toUpperCase())));
        }
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(service.listByStatus(IntegrationPartner.PartnerStatus.valueOf(status.toUpperCase())));
        }
        return ResponseEntity.ok(service.searchByName(query));
    }
}
