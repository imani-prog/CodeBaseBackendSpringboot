package com.example.codebasebackend.controllers;

import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.AssistanceResponse;
import com.example.codebasebackend.services.AssistanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assist")
@RequiredArgsConstructor
public class AmbulanceDispatchController {

    private final AssistanceService assistanceService;

    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "Assistance", includeArgs = true)
    @PostMapping
    public ResponseEntity<AssistanceResponse> request(@Valid @RequestBody AssistanceRequest request) {
        return ResponseEntity.ok(assistanceService.requestAssistance(request));
    }
}
