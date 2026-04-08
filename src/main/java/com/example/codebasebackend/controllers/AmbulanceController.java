package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.dto.request.LocationUpdateRequest;
import com.example.codebasebackend.dto.response.AmbulanceDispatchResponse;
import com.example.codebasebackend.dto.response.AmbulanceStatistics;
import com.example.codebasebackend.dto.response.AmbulanceTrackingResponse;
import com.example.codebasebackend.services.AmbulanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ambulances")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AmbulanceController {

    private final AmbulanceService ambulanceService;

    @PostMapping
    public ResponseEntity<Ambulances> addAmbulance(@RequestBody Ambulances ambulance) {
        return ResponseEntity.ok(ambulanceService.addAmbulance(ambulance));
    }

    @GetMapping
    public ResponseEntity<List<Ambulances>> getAllAmbulances() {
        return ResponseEntity.ok(ambulanceService.getAllAmbulances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ambulances> getAmbulanceById(@PathVariable Long id) {
        return ResponseEntity.ok(ambulanceService.getAmbulanceById(id));
    }

    @GetMapping("/by-plate/{vehiclePlate}")
    public ResponseEntity<Ambulances> getAmbulanceByVehiclePlate(@PathVariable String vehiclePlate) {
        return ResponseEntity.ok(ambulanceService.getAmbulanceByVehiclePlate(vehiclePlate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ambulances> updateAmbulance(@PathVariable Long id, @RequestBody Ambulances ambulance) {
        return ResponseEntity.ok(ambulanceService.updateAmbulance(id, ambulance));
    }

    @PutMapping("/by-plate/{vehiclePlate}")
    public ResponseEntity<Ambulances> updateAmbulanceByVehiclePlate(@PathVariable String vehiclePlate, @RequestBody Ambulances ambulance) {
        return ResponseEntity.ok(ambulanceService.updateAmbulanceByVehiclePlate(vehiclePlate, ambulance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAmbulance(@PathVariable Long id) {
        ambulanceService.deleteAmbulance(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== STATUS MANAGEMENT ====================
    @GetMapping("/available")
    public ResponseEntity<List<Ambulances>> getAvailableAmbulances() {
        List<Ambulances> available = ambulanceService.getAvailableAmbulances();
        return ResponseEntity.ok(available);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ambulances>> getAmbulancesByStatus(@PathVariable String status) {
        List<Ambulances> ambulances = ambulanceService.getAmbulancesByStatus(status);
        return ResponseEntity.ok(ambulances);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Ambulances> updateStatus(
        @PathVariable Long id,
        @RequestParam String status
    ) {
        Ambulances updated = ambulanceService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    // ==================== LOCATION & TRACKING ====================
    @PostMapping("/{id}/location")
    public ResponseEntity<AmbulanceTrackingResponse> updateLocation(
        @PathVariable Long id,
        @RequestBody LocationUpdateRequest request
    ) {
        AmbulanceTrackingResponse tracking = ambulanceService.updateLocation(id, request);
        return ResponseEntity.ok(tracking);
    }

    @GetMapping("/{id}/tracking-history")
    public ResponseEntity<List<AmbulanceTrackingResponse>> getTrackingHistory(
        @PathVariable Long id,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        List<AmbulanceTrackingResponse> history = ambulanceService.getTrackingHistory(id, from, to);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}/current-location")
    public ResponseEntity<AmbulanceTrackingResponse> getCurrentLocation(@PathVariable Long id) {
        AmbulanceTrackingResponse current = ambulanceService.getCurrentLocation(id);
        return ResponseEntity.ok(current);
    }

    @GetMapping("/tracking/active")
    public ResponseEntity<List<AmbulanceTrackingResponse>> getAllActiveTracking() {
        List<AmbulanceTrackingResponse> active = ambulanceService.getAllActiveTracking();
        return ResponseEntity.ok(active);
    }

    // ==================== MAINTENANCE ====================
    @GetMapping("/maintenance-due")
    public ResponseEntity<List<Ambulances>> getMaintenanceDue() {
        List<Ambulances> due = ambulanceService.getMaintenanceDue();
        return ResponseEntity.ok(due);
    }

    // ==================== STATISTICS ====================
    @GetMapping("/statistics")
    public ResponseEntity<AmbulanceStatistics> getStatistics() {
        AmbulanceStatistics stats = ambulanceService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    // ==================== SEARCH & FILTERS ====================
    @GetMapping("/search")
    public ResponseEntity<List<Ambulances>> searchAmbulances(@RequestParam String query) {
        List<Ambulances> results = ambulanceService.searchAmbulances(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Ambulances>> getAmbulancesByType(@PathVariable String type) {
        List<Ambulances> ambulances = ambulanceService.getAmbulancesByType(type);
        return ResponseEntity.ok(ambulances);
    }

    // ==================== DISPATCH HISTORY ====================
    @GetMapping("/{id}/dispatches")
    public ResponseEntity<List<AmbulanceDispatchResponse>> getDispatchHistory(@PathVariable Long id) {
        List<AmbulanceDispatchResponse> dispatches = ambulanceService.getDispatchHistory(id);
        return ResponseEntity.ok(dispatches);
    }
}
