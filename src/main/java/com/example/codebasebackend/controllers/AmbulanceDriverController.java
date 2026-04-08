package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.AmbulanceDriver;
import com.example.codebasebackend.dto.response.AmbulanceDriverResponse;
import com.example.codebasebackend.services.AmbulanceDriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AmbulanceDriverController {

    private final AmbulanceDriverService driverService;

    @PostMapping
    public ResponseEntity<AmbulanceDriverResponse> addDriver(@RequestBody AmbulanceDriver driver) {
        return ResponseEntity.ok(driverService.addDriver(driver));
    }

    @GetMapping
    public ResponseEntity<List<AmbulanceDriverResponse>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmbulanceDriverResponse> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<AmbulanceDriverResponse>> getAvailableDrivers() {
        return ResponseEntity.ok(driverService.getAvailableDrivers());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AmbulanceDriverResponse>> getDriversByStatus(@PathVariable String status) {
        return ResponseEntity.ok(driverService.getDriversByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AmbulanceDriverResponse> updateDriver(
        @PathVariable Long id,
        @RequestBody AmbulanceDriver driver
    ) {
        return ResponseEntity.ok(driverService.updateDriver(id, driver));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AmbulanceDriverResponse> updateDriverStatus(
        @PathVariable Long id,
        @RequestParam String status
    ) {
        return ResponseEntity.ok(driverService.updateDriverStatus(id, status));
    }

    @PatchMapping("/{id}/assign-ambulance")
    public ResponseEntity<AmbulanceDriverResponse> assignToAmbulance(
        @PathVariable Long id,
        @RequestParam Long ambulanceId
    ) {
        return ResponseEntity.ok(driverService.assignToAmbulance(id, ambulanceId));
    }

    @PatchMapping("/{id}/unassign-ambulance")
    public ResponseEntity<AmbulanceDriverResponse> unassignFromAmbulance(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.unassignFromAmbulance(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}
