package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.dto.ServiceOrderRequest;
import com.example.codebasebackend.dto.ServiceOrderResponse;
import com.example.codebasebackend.services.ServiceOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "ServiceOrder", entityIdExpression = "#result.body.id", includeArgs = true, includeResult = true)
    @PostMapping
    public ResponseEntity<ServiceOrderResponse> placeOrder(@Valid @RequestBody ServiceOrderRequest request) {
        return ResponseEntity.ok(serviceOrderService.placeOrder(request));
    }

    @Auditable(eventType = AuditLog.EventType.READ, entityType = "ServiceOrder", entityIdExpression = "#id", includeArgs = true)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(serviceOrderService.get(id));
    }

    @Auditable(eventType = AuditLog.EventType.READ, entityType = "ServiceOrder", entityIdExpression = "#patientId", includeArgs = true)
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ServiceOrderResponse>> listByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(serviceOrderService.listByPatient(patientId));
    }
}

