package com.example.codebasebackend.controllers;

import com.example.codebasebackend.dto.SpecialtyRequest;
import com.example.codebasebackend.dto.SpecialtyResponse;
import com.example.codebasebackend.services.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SpecialtyController {

    private final SpecialtyService specialtyService;


    @PostMapping
    public ResponseEntity<SpecialtyResponse> createSpecialty(@Valid @RequestBody SpecialtyRequest request) {
        SpecialtyResponse response = specialtyService.createSpecialty(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> getAllSpecialties() {
        List<SpecialtyResponse> specialties = specialtyService.getAllSpecialties();
        return ResponseEntity.ok(specialties);
    }


    @GetMapping("/active")
    public ResponseEntity<List<SpecialtyResponse>> getActiveSpecialties() {
        List<SpecialtyResponse> specialties = specialtyService.getActiveSpecialties();
        return ResponseEntity.ok(specialties);
    }


    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> getSpecialtyById(@PathVariable Long id) {
        SpecialtyResponse specialty = specialtyService.getSpecialtyById(id);
        return ResponseEntity.ok(specialty);
    }


    @GetMapping("/by-name/{name}")
    public ResponseEntity<SpecialtyResponse> getSpecialtyByName(@PathVariable String name) {
        SpecialtyResponse specialty = specialtyService.getSpecialtyByName(name);
        return ResponseEntity.ok(specialty);
    }


    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> updateSpecialty(
            @PathVariable Long id,
            @Valid @RequestBody SpecialtyRequest request) {
        SpecialtyResponse specialty = specialtyService.updateSpecialty(id, request);
        return ResponseEntity.ok(specialty);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<SpecialtyResponse> activateSpecialty(@PathVariable Long id) {
        SpecialtyResponse specialty = specialtyService.activateSpecialty(id);
        return ResponseEntity.ok(specialty);
    }


    @PostMapping("/{id}/deactivate")
    public ResponseEntity<SpecialtyResponse> deactivateSpecialty(@PathVariable Long id) {
        SpecialtyResponse specialty = specialtyService.deactivateSpecialty(id);
        return ResponseEntity.ok(specialty);
    }
}
