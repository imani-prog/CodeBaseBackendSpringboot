package com.example.codebasebackend.controllers;

import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.dto.CommunityHealthWorkerRequest;
import com.example.codebasebackend.dto.CommunityHealthWorkerResponse;
import com.example.codebasebackend.dto.LocationUpdateRequest;
import com.example.codebasebackend.dto.PerformanceMetricsRequest;
import com.example.codebasebackend.services.CommunityHealthWorkersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    /**
     * Update CHW performance metrics
     */
    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "CHW",
              entityIdExpression = "#id", includeArgs = true)
    @PatchMapping("/{id}/performance")
    public ResponseEntity<CommunityHealthWorkerResponse> updatePerformanceMetrics(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceMetricsRequest request) {
        return ResponseEntity.ok(service.updatePerformanceMetrics(id, request));
    }

    /**
     * Get CHWs by region
     */
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "CHW")
    @GetMapping("/by-region/{region}")
    public ResponseEntity<List<CommunityHealthWorkerResponse>> getByRegion(
            @PathVariable String region) {
        return ResponseEntity.ok(service.findByRegion(region));
    }

    /**
     * Get CHWs by status
     */
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "CHW")
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<CommunityHealthWorkerResponse>> getByStatus(
            @PathVariable String status) {
        try {
            CommunityHealthWorkers.Status chwStatus = CommunityHealthWorkers.Status.valueOf(status.toUpperCase());
            return ResponseEntity.ok(service.findByStatus(chwStatus));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid status: " + status);
        }
    }

    /**
     * Get CHWs with pagination and filtering
     */
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "CHW")
    @GetMapping("/search")
    public ResponseEntity<Page<CommunityHealthWorkerResponse>> search(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        CommunityHealthWorkers.Status chwStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                chwStatus = CommunityHealthWorkers.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(BAD_REQUEST, "Invalid status: " + status);
            }
        }
        return ResponseEntity.ok(service.search(region, chwStatus, city, page, size, sortBy, sortDirection));
    }
}


