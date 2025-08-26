package com.example.codebasebackend.controllers;

import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.AssistanceResponse;
import com.example.codebasebackend.services.AmbulanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assist")
@RequiredArgsConstructor
public class AmbulanceDispatchController {

    private final AmbulanceService ambulanceService;

    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "Assistance", includeArgs = true)
    @PostMapping
    public ResponseEntity<AssistanceResponse> request(@Valid @RequestBody AssistanceRequest request) {
        return ResponseEntity.ok(ambulanceService.createDispatch(request));
    }

    @GetMapping
    public ResponseEntity<List<AssistanceResponse>> getAllDispatches() {
        return ResponseEntity.ok(ambulanceService.getAllDispatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssistanceResponse> getDispatchById(@PathVariable Long id) {
        return ResponseEntity.ok(ambulanceService.getDispatchById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssistanceResponse> updateDispatch(@PathVariable Long id, @Valid @RequestBody AssistanceRequest request) {
        return ResponseEntity.ok(ambulanceService.updateDispatch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDispatch(@PathVariable Long id) {
        ambulanceService.deleteDispatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/track")
    public ResponseEntity<AssistanceResponse> trackDispatch(@PathVariable Long id) {
        return ResponseEntity.ok(ambulanceService.trackDispatch(id));
    }
}
