package com.example.codebasebackend.controllers;

import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.dto.LocationUpdateRequest;
import com.example.codebasebackend.services.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "Patient", entityIdExpression = "#id", includeArgs = true)
    @PatchMapping("/{id}/location")
    public ResponseEntity<Void> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationUpdateRequest request) {
        patientService.updateLocation(id, request.getLatitude(), request.getLongitude());
        return ResponseEntity.noContent().build();
    }
}
