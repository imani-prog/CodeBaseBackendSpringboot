package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.AmbulanceDriver;
import com.example.codebasebackend.services.AmbulanceDriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class AmbulanceDriverController {

    private final AmbulanceDriverService driverService;

    @PostMapping
    public ResponseEntity<AmbulanceDriver> addDriver(@RequestBody AmbulanceDriver driver) {
        return ResponseEntity.ok(driverService.addDriver(driver));
    }

    @GetMapping
    public ResponseEntity<List<AmbulanceDriver>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmbulanceDriver> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<AmbulanceDriver>> getAvailableDrivers() {
        return ResponseEntity.ok(driverService.getAvailableDrivers());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AmbulanceDriver>> getDriversByStatus(@PathVariable String status) {
        return ResponseEntity.ok(driverService.getDriversByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AmbulanceDriver> updateDriver(
        @PathVariable Long id,
        @RequestBody AmbulanceDriver driver
    ) {
        return ResponseEntity.ok(driverService.updateDriver(id, driver));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AmbulanceDriver> updateDriverStatus(
        @PathVariable Long id,
        @RequestParam String status
    ) {
        return ResponseEntity.ok(driverService.updateDriverStatus(id, status));
    }

    @PatchMapping("/{id}/assign-ambulance")
    public ResponseEntity<AmbulanceDriver> assignToAmbulance(
        @PathVariable Long id,
        @RequestParam Long ambulanceId
    ) {
        return ResponseEntity.ok(driverService.assignToAmbulance(id, ambulanceId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}
