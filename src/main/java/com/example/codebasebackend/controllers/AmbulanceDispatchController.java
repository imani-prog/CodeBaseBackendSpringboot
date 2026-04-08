package com.example.codebasebackend.controllers;

import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.response.AmbulanceDispatchResponse;
import com.example.codebasebackend.services.AmbulanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assist")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AmbulanceDispatchController {

    private final AmbulanceService ambulanceService;

    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "Assistance", includeArgs = true)
    @PostMapping
    public ResponseEntity<AmbulanceDispatchResponse> request(@Valid @RequestBody AssistanceRequest request) {
        return ResponseEntity.ok(ambulanceService.createDispatch(request));
    }

    @GetMapping
    public ResponseEntity<List<AmbulanceDispatchResponse>> getAllDispatches() {
        return ResponseEntity.ok(ambulanceService.getAllDispatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmbulanceDispatchResponse> getDispatchById(@PathVariable Long id) {
        return ResponseEntity.ok(ambulanceService.getDispatchById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AmbulanceDispatchResponse> updateDispatch(@PathVariable Long id, @Valid @RequestBody AssistanceRequest request) {
        return ResponseEntity.ok(ambulanceService.updateDispatch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDispatch(@PathVariable Long id) {
        ambulanceService.deleteDispatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/track")
    public ResponseEntity<AmbulanceDispatchResponse> trackDispatch(@PathVariable Long id) {
        return ResponseEntity.ok(ambulanceService.trackDispatch(id));
    }
}
