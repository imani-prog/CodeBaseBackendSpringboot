package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.dto.LocationUpdateRequest;
import com.example.codebasebackend.services.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // List all patients
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Patient", includeArgs = true)
    @GetMapping
    public ResponseEntity<List<Patient>> listPatients() {
        return ResponseEntity.ok(patientService.listPatients());
    }

    // Get one patient by id
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Patient", entityIdExpression = "#id", includeArgs = true)
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatient(id));
    }

    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "Patient", entityIdExpression = "#id", includeArgs = true)
    @PatchMapping("/{id}/location")
    public ResponseEntity<Void> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationUpdateRequest request) {
        patientService.updateLocation(id, request.getLatitude(), request.getLongitude());
        return ResponseEntity.noContent().build();
    }

    // POST mapping for creating a patient
    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "Patient", entityIdExpression = "#result.body.id", includeArgs = true, includeResult = true)
    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        Patient savedPatient = patientService.savePatient(patient);
        return ResponseEntity.ok(savedPatient);
    }

    // Full update (PUT)
    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "Patient", entityIdExpression = "#id", includeArgs = true, includeResult = true)
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        Patient updated = patientService.updatePatient(id, patient);
        return ResponseEntity.ok(updated);
    }

    // Delete
    @Auditable(eventType = AuditLog.EventType.DELETE, entityType = "Patient", entityIdExpression = "#id", includeArgs = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
