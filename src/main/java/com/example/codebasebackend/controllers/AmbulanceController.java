package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.dto.request.LocationUpdateRequest;
import com.example.codebasebackend.dto.response.AmbulanceDispatchResponse;
import com.example.codebasebackend.dto.response.AmbulanceResponse;
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
public class AmbulanceController {

    private final AmbulanceService ambulanceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AmbulanceResponse> addAmbulance(@RequestBody Ambulances ambulance) {
        return ResponseEntity.ok(ambulanceService.addAmbulance(ambulance));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<List<AmbulanceResponse>> getAllAmbulances() {
        return ResponseEntity.ok(ambulanceService.getAllAmbulances());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<AmbulanceResponse> getAmbulanceById(@PathVariable Long id) {
        return ResponseEntity.ok(ambulanceService.getAmbulanceById(id));
    }

    @GetMapping("/by-plate/{vehiclePlate}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<AmbulanceResponse> getAmbulanceByVehiclePlate(@PathVariable String vehiclePlate) {
        return ResponseEntity.ok(ambulanceService.getAmbulanceByVehiclePlate(vehiclePlate));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AmbulanceResponse> updateAmbulance(@PathVariable Long id, @RequestBody Ambulances ambulance) {
        return ResponseEntity.ok(ambulanceService.updateAmbulance(id, ambulance));
    }

    @PutMapping("/by-plate/{vehiclePlate}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AmbulanceResponse> updateAmbulanceByVehiclePlate(@PathVariable String vehiclePlate, @RequestBody Ambulances ambulance) {
        return ResponseEntity.ok(ambulanceService.updateAmbulanceByVehiclePlate(vehiclePlate, ambulance));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAmbulance(@PathVariable Long id) {
        ambulanceService.deleteAmbulance(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== STATUS MANAGEMENT ====================
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<List<AmbulanceResponse>> getAvailableAmbulances() {
        return ResponseEntity.ok(ambulanceService.getAvailableAmbulances());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<List<AmbulanceResponse>> getAmbulancesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ambulanceService.getAmbulancesByStatus(status));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AmbulanceResponse> updateStatus(
        @PathVariable Long id,
        @RequestParam String status
    ) {
        return ResponseEntity.ok(ambulanceService.updateStatus(id, status));
    }

    // ==================== LOCATION & TRACKING ====================
    @PostMapping("/{id}/location")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AmbulanceTrackingResponse> updateLocation(
        @PathVariable Long id,
        @RequestBody LocationUpdateRequest request
    ) {
        return ResponseEntity.ok(ambulanceService.updateLocation(id, request));
    }

    @GetMapping("/{id}/tracking-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<List<AmbulanceTrackingResponse>> getTrackingHistory(
        @PathVariable Long id,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        return ResponseEntity.ok(ambulanceService.getTrackingHistory(id, from, to));
    }

    @GetMapping("/{id}/current-location")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<AmbulanceTrackingResponse> getCurrentLocation(@PathVariable Long id) {
        return ResponseEntity.ok(ambulanceService.getCurrentLocation(id));
    }

    @GetMapping("/tracking/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<List<AmbulanceTrackingResponse>> getAllActiveTracking() {
        return ResponseEntity.ok(ambulanceService.getAllActiveTracking());
    }

    // ==================== MAINTENANCE ====================
    @GetMapping("/maintenance-due")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AmbulanceResponse>> getMaintenanceDue() {
        return ResponseEntity.ok(ambulanceService.getMaintenanceDue());
    }

    // ==================== STATISTICS ====================
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AmbulanceStatistics> getStatistics() {
        return ResponseEntity.ok(ambulanceService.getStatistics());
    }

    // ==================== SEARCH & FILTERS ====================
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<List<AmbulanceResponse>> searchAmbulances(@RequestParam String query) {
        return ResponseEntity.ok(ambulanceService.searchAmbulances(query));
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<List<AmbulanceResponse>> getAmbulancesByType(@PathVariable String type) {
        return ResponseEntity.ok(ambulanceService.getAmbulancesByType(type));
    }

    // ==================== DISPATCH HISTORY ====================
    @GetMapping("/{id}/dispatches")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<List<AmbulanceDispatchResponse>> getDispatchHistory(@PathVariable Long id) {
        return ResponseEntity.ok(ambulanceService.getDispatchHistory(id));
    }
}
