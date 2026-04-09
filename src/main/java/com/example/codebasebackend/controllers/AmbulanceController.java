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
@PreAuthorize("hasRole('ADMIN')")
public class AmbulanceController {

    private final AmbulanceService ambulanceService;

    @PostMapping
    public ResponseEntity<AmbulanceResponse> addAmbulance(@RequestBody Ambulances ambulance) {
        return ResponseEntity.ok(toResponse(ambulanceService.addAmbulance(ambulance)));
    }

    @GetMapping
    public ResponseEntity<List<AmbulanceResponse>> getAllAmbulances() {
        return ResponseEntity.ok(ambulanceService.getAllAmbulances().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmbulanceResponse> getAmbulanceById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(ambulanceService.getAmbulanceById(id)));
    }

    @GetMapping("/by-plate/{vehiclePlate}")
    public ResponseEntity<AmbulanceResponse> getAmbulanceByVehiclePlate(@PathVariable String vehiclePlate) {
        return ResponseEntity.ok(toResponse(ambulanceService.getAmbulanceByVehiclePlate(vehiclePlate)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AmbulanceResponse> updateAmbulance(@PathVariable Long id, @RequestBody Ambulances ambulance) {
        return ResponseEntity.ok(toResponse(ambulanceService.updateAmbulance(id, ambulance)));
    }

    @PutMapping("/by-plate/{vehiclePlate}")
    public ResponseEntity<AmbulanceResponse> updateAmbulanceByVehiclePlate(@PathVariable String vehiclePlate, @RequestBody Ambulances ambulance) {
        return ResponseEntity.ok(toResponse(ambulanceService.updateAmbulanceByVehiclePlate(vehiclePlate, ambulance)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAmbulance(@PathVariable Long id) {
        ambulanceService.deleteAmbulance(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== STATUS MANAGEMENT ====================
    @GetMapping("/available")
    public ResponseEntity<List<AmbulanceResponse>> getAvailableAmbulances() {
        List<AmbulanceResponse> available = ambulanceService.getAvailableAmbulances().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(available);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AmbulanceResponse>> getAmbulancesByStatus(@PathVariable String status) {
        List<AmbulanceResponse> ambulances = ambulanceService.getAmbulancesByStatus(status).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ambulances);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AmbulanceResponse> updateStatus(
        @PathVariable Long id,
        @RequestParam String status
    ) {
        AmbulanceResponse updated = toResponse(ambulanceService.updateStatus(id, status));
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
    public ResponseEntity<List<AmbulanceResponse>> getMaintenanceDue() {
        List<AmbulanceResponse> due = ambulanceService.getMaintenanceDue().stream().map(this::toResponse).toList();
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
    public ResponseEntity<List<AmbulanceResponse>> searchAmbulances(@RequestParam String query) {
        List<AmbulanceResponse> results = ambulanceService.searchAmbulances(query).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<AmbulanceResponse>> getAmbulancesByType(@PathVariable String type) {
        List<AmbulanceResponse> ambulances = ambulanceService.getAmbulancesByType(type).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ambulances);
    }

    // ==================== DISPATCH HISTORY ====================
    @GetMapping("/{id}/dispatches")
    public ResponseEntity<List<AmbulanceDispatchResponse>> getDispatchHistory(@PathVariable Long id) {
        List<AmbulanceDispatchResponse> dispatches = ambulanceService.getDispatchHistory(id);
        return ResponseEntity.ok(dispatches);
    }

    private AmbulanceResponse toResponse(Ambulances ambulance) {
        AmbulanceResponse.DriverSummary driverSummary = null;
        if (ambulance.getCurrentDriver() != null) {
            driverSummary = AmbulanceResponse.DriverSummary.builder()
                .id(ambulance.getCurrentDriver().getId())
                .name(ambulance.getCurrentDriver().getName())
                .status(ambulance.getCurrentDriver().getStatus() != null ? ambulance.getCurrentDriver().getStatus().name() : null)
                .phone(ambulance.getCurrentDriver().getPhone())
                .build();
        }

        return AmbulanceResponse.builder()
            .id(ambulance.getId())
            .vehiclePlate(ambulance.getVehiclePlate())
            .driverName(ambulance.getDriverName())
            .driverPhone(ambulance.getDriverPhone())
            .status(ambulance.getStatus() != null ? ambulance.getStatus().name() : null)
            .medicName(ambulance.getMedicName())
            .notes(ambulance.getNotes())
            .registrationNumber(ambulance.getRegistrationNumber())
            .model(ambulance.getModel())
            .year(ambulance.getYear())
            .fuelType(ambulance.getFuelType() != null ? ambulance.getFuelType().name() : null)
            .capacity(ambulance.getCapacity())
            .equippedForICU(ambulance.isEquippedForICU())
            .gpsEnabled(ambulance.isGpsEnabled())
            .insurancePolicyNumber(ambulance.getInsurancePolicyNumber())
            .insuranceProvider(ambulance.getInsuranceProvider())
            .type(ambulance.getType() != null ? ambulance.getType().name() : null)
            .currentLocation(ambulance.getCurrentLocation())
            .currentLatitude(ambulance.getCurrentLatitude())
            .currentLongitude(ambulance.getCurrentLongitude())
            .lastMaintenanceDate(ambulance.getLastMaintenanceDate())
            .nextMaintenanceDate(ambulance.getNextMaintenanceDate())
            .lastMaintenanceMileage(ambulance.getLastMaintenanceMileage())
            .mileage(ambulance.getMileage())
            .fuelLevel(ambulance.getFuelLevel())
            .lastDispatchTime(ambulance.getLastDispatchTime())
            .totalDispatches(ambulance.getTotalDispatches())
            .averageResponseMinutes(ambulance.getAverageResponseMinutes())
            .equipmentList(ambulance.getEquipmentList())
            .imageUrl(ambulance.getImageUrl())
            .createdAt(ambulance.getCreatedAt())
            .updatedAt(ambulance.getUpdatedAt())
            .currentDriver(driverSummary)
            .build();
    }
}
