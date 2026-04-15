package com.example.codebasebackend.controllers;

import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.response.AmbulanceDispatchResponse;
import com.example.codebasebackend.repositories.PatientRepository;
import com.example.codebasebackend.services.AmbulanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/api/assist")
@RequiredArgsConstructor
public class AmbulanceDispatchController {

    private final AmbulanceService ambulanceService;
    private final PatientRepository patientRepository;

    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "Assistance", includeArgs = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    @PostMapping
    public ResponseEntity<AmbulanceDispatchResponse> request(@Valid @RequestBody AssistanceRequest request) {
        return ResponseEntity.ok(ambulanceService.createDispatch(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    @GetMapping
    public ResponseEntity<List<AmbulanceDispatchResponse>> getAllDispatches(Authentication authentication) {
        List<AmbulanceDispatchResponse> dispatches = ambulanceService.getAllDispatches();

        boolean isPatient = authentication != null
            && authentication.getAuthorities().stream().anyMatch(a -> "ROLE_PATIENT".equals(a.getAuthority()));
        if (!isPatient) {
            return ResponseEntity.ok(dispatches);
        }

        Patient patient = patientRepository.findByUserUsername(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Patient profile not found"));

        List<AmbulanceDispatchResponse> ownDispatches = dispatches.stream()
            .filter(dispatch -> dispatch.getPatientId() != null && dispatch.getPatientId().equals(patient.getId()))
            .toList();
        return ResponseEntity.ok(ownDispatches);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AmbulanceDispatchResponse> getDispatchById(@PathVariable Long id) {
        return ResponseEntity.ok(ambulanceService.getDispatchById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AmbulanceDispatchResponse> updateDispatch(@PathVariable Long id, @Valid @RequestBody AssistanceRequest request) {
        return ResponseEntity.ok(ambulanceService.updateDispatch(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDispatch(@PathVariable Long id) {
        ambulanceService.deleteDispatch(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/track")
    public ResponseEntity<AmbulanceDispatchResponse> trackDispatch(@PathVariable Long id) {
        return ResponseEntity.ok(ambulanceService.trackDispatch(id));
    }
}
