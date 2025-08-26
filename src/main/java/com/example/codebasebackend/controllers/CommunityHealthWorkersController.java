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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
    public ResponseEntity<CommunityHealthWorkerResponse> nearest(
            @RequestParam(value = "lat", required = false) java.math.BigDecimal lat,
            @RequestParam(value = "lon", required = false) java.math.BigDecimal lon,
            @RequestParam(value = "latitude", required = false) java.math.BigDecimal latitude,
            @RequestParam(value = "longitude", required = false) java.math.BigDecimal longitude,
            @RequestParam(value = "hospitalId", required = false) Long hospitalId,
            @RequestParam(value = "radiusKm", required = false) java.math.BigDecimal radiusKm) {
        java.math.BigDecimal effLat = lat != null ? lat : latitude;
        java.math.BigDecimal effLon = lon != null ? lon : longitude;
        if (effLat == null || effLon == null) throw new ResponseStatusException(BAD_REQUEST, "lat/lon (or latitude/longitude) are required");
        if (radiusKm != null) {
            return ResponseEntity.ok(service.findNearestAvailable(effLat, effLon, hospitalId, radiusKm));
        }
        return ResponseEntity.ok(service.findNearestAvailable(effLat, effLon, hospitalId));
    }
}
