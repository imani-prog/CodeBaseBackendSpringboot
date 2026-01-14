package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.services.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    // List hospitals
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Hospital", includeArgs = true)
    @GetMapping
    public ResponseEntity<List<Hospital>> listHospitals() {
        return ResponseEntity.ok(hospitalService.listHospitals());
    }

    // Get hospital by id
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Hospital", entityIdExpression = "#id", includeArgs = true)
    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getHospital(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getHospital(id));
    }

    // Create hospital
    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "Hospital", entityIdExpression = "#result.body.id", includeArgs = true, includeResult = true)
    @PostMapping
    public ResponseEntity<Hospital> createHospital(@Valid @RequestBody Hospital hospital) {
        Hospital saved = hospitalService.createHospital(hospital);
        return ResponseEntity.ok(saved);
    }

    // Update hospital
    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "Hospital", entityIdExpression = "#id", includeArgs = true, includeResult = true)
    @PutMapping("/{id}")
    public ResponseEntity<Hospital> updateHospital(@PathVariable Long id, @Valid @RequestBody Hospital hospital) {
        Hospital updated = hospitalService.updateHospital(id, hospital);
        return ResponseEntity.ok(updated);
    }

    // Delete hospital
    @Auditable(eventType = AuditLog.EventType.DELETE, entityType = "Hospital", entityIdExpression = "#id", includeArgs = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        hospitalService.deleteHospital(id);
        return ResponseEntity.noContent().build();
    }

    // Get hospital by business code (e.g., HS001)
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Hospital", includeArgs = true)
    @GetMapping("/by-code/{code}")
    public ResponseEntity<Hospital> getHospitalByCode(@PathVariable String code) {
        return ResponseEntity.ok(hospitalService.getHospitalByCode(code));
    }

    // Get hospitals by facility
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Hospital", includeArgs = true)
    @GetMapping("/by-facility/{facility}")
    public ResponseEntity<List<Hospital>> getHospitalsByFacility(@PathVariable String facility) {
        return ResponseEntity.ok(hospitalService.getHospitalsByFacility(facility));
    }
}
