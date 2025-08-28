package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.services.AmbulanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ambulances")
@RequiredArgsConstructor
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
}
