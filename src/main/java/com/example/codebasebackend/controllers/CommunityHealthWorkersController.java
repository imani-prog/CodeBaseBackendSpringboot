package com.example.codebasebackend.controllers;

import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.dto.CommunityHealthWorkerRequest;
import com.example.codebasebackend.dto.CommunityHealthWorkerResponse;
import com.example.codebasebackend.dto.LocationUpdateRequest;
import com.example.codebasebackend.services.CommunityHealthWorkersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chw")
@RequiredArgsConstructor
public class CommunityHealthWorkersController {

    private final CommunityHealthWorkersService service;

    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "CHW", entityIdExpression = "#result.id", includeArgs = true)
    @PostMapping
    public ResponseEntity<CommunityHealthWorkerResponse> create(@Valid @RequestBody CommunityHealthWorkerRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @Auditable(eventType = AuditLog.EventType.READ, entityType = "CHW", entityIdExpression = "#id")
    @GetMapping("/{id}")
    public ResponseEntity<CommunityHealthWorkerResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @Auditable(eventType = AuditLog.EventType.READ, entityType = "CHW")
    @GetMapping
    public ResponseEntity<List<CommunityHealthWorkerResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "CHW", entityIdExpression = "#id", includeArgs = true)
    @PutMapping("/{id}")
    public ResponseEntity<CommunityHealthWorkerResponse> update(@PathVariable Long id,
                                                                @Valid @RequestBody CommunityHealthWorkerRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Auditable(eventType = AuditLog.EventType.DELETE, entityType = "CHW", entityIdExpression = "#id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "CHW", entityIdExpression = "#id", includeArgs = true)
    @PatchMapping("/{id}/location")
    public ResponseEntity<CommunityHealthWorkerResponse> updateLocation(@PathVariable Long id,
                                                                        @Valid @RequestBody LocationUpdateRequest request) {
        return ResponseEntity.ok(service.updateLocation(id, request.getLatitude(), request.getLongitude()));
    }

    @Auditable(eventType = AuditLog.EventType.READ, entityType = "CHW")
    @GetMapping("/nearest")
    public ResponseEntity<CommunityHealthWorkerResponse> nearest(@RequestParam("lat") java.math.BigDecimal lat,
                                                                 @RequestParam("lon") java.math.BigDecimal lon,
                                                                 @RequestParam(value = "hospitalId", required = false) Long hospitalId) {
        return ResponseEntity.ok(service.findNearestAvailable(lat, lon, hospitalId));
    }
}
