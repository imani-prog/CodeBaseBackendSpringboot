package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.DoctorStatus;
import com.example.codebasebackend.dto.DoctorRequest;
import com.example.codebasebackend.dto.DoctorResponse;
import com.example.codebasebackend.services.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * Create a new doctor
     * POST /api/doctors
     */
    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest request) {
        DoctorResponse response = doctorService.createDoctor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all doctors with pagination
     * GET /api/doctors?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> getAllDoctors(
            @PageableDefault(size = 10, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<DoctorResponse> doctors = doctorService.getAllDoctors(pageable);
        return ResponseEntity.ok(doctors);
    }

    /**
     * Get doctor by ID
     * GET /api/doctors/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        DoctorResponse doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Get doctor by doctor ID (e.g., DOC-001)
     * GET /api/doctors/by-doctor-id/{doctorId}
     */
    @GetMapping("/by-doctor-id/{doctorId}")
    public ResponseEntity<DoctorResponse> getDoctorByDoctorId(@PathVariable String doctorId) {
        DoctorResponse doctor = doctorService.getDoctorByDoctorId(doctorId);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Get doctor by email
     * GET /api/doctors/by-email/{email}
     */
    @GetMapping("/by-email/{email}")
    public ResponseEntity<DoctorResponse> getDoctorByEmail(@PathVariable String email) {
        DoctorResponse doctor = doctorService.getDoctorByEmail(email);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Update doctor
     * PUT /api/doctors/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequest request) {
        DoctorResponse doctor = doctorService.updateDoctor(id, request);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Delete doctor
     * DELETE /api/doctors/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update doctor status
     * PATCH /api/doctors/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<DoctorResponse> updateDoctorStatus(
            @PathVariable Long id,
            @RequestParam DoctorStatus status) {
        DoctorResponse doctor = doctorService.updateDoctorStatus(id, status);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Set doctor as available
     * POST /api/doctors/{id}/available
     */
    @PostMapping("/{id}/available")
    public ResponseEntity<DoctorResponse> setDoctorAvailable(@PathVariable Long id) {
        DoctorResponse doctor = doctorService.setDoctorAvailable(id);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Set doctor as busy
     * POST /api/doctors/{id}/busy
     */
    @PostMapping("/{id}/busy")
    public ResponseEntity<DoctorResponse> setDoctorBusy(@PathVariable Long id) {
        DoctorResponse doctor = doctorService.setDoctorBusy(id);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Set doctor as offline
     * POST /api/doctors/{id}/offline
     */
    @PostMapping("/{id}/offline")
    public ResponseEntity<DoctorResponse> setDoctorOffline(@PathVariable Long id) {
        DoctorResponse doctor = doctorService.setDoctorOffline(id);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Set doctor on break
     * POST /api/doctors/{id}/break
     */
    @PostMapping("/{id}/break")
    public ResponseEntity<DoctorResponse> setDoctorOnBreak(@PathVariable Long id) {
        DoctorResponse doctor = doctorService.setDoctorOnBreak(id);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Search doctors
     * GET /api/doctors/search?term=John
     */
    @GetMapping("/search")
    public ResponseEntity<Page<DoctorResponse>> searchDoctors(
            @RequestParam String term,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<DoctorResponse> doctors = doctorService.searchDoctors(term, pageable);
        return ResponseEntity.ok(doctors);
    }

    /**
     * Get doctors by status
     * GET /api/doctors/by-status/{status}
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<DoctorResponse>> getDoctorsByStatus(@PathVariable DoctorStatus status) {
        List<DoctorResponse> doctors = doctorService.getDoctorsByStatus(status);
        return ResponseEntity.ok(doctors);
    }

    /**
     * Get doctors by specialty
     * GET /api/doctors/by-specialty/{specialtyId}
     */
    @GetMapping("/by-specialty/{specialtyId}")
    public ResponseEntity<Page<DoctorResponse>> getDoctorsBySpecialty(
            @PathVariable Long specialtyId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<DoctorResponse> doctors = doctorService.getDoctorsBySpecialty(specialtyId, pageable);
        return ResponseEntity.ok(doctors);
    }

    /**
     * Get available doctors
     * GET /api/doctors/available
     */
    @GetMapping("/available")
    public ResponseEntity<List<DoctorResponse>> getAvailableDoctors() {
        List<DoctorResponse> doctors = doctorService.getAvailableDoctors();
        return ResponseEntity.ok(doctors);
    }

    /**
     * Get online doctors
     * GET /api/doctors/online
     */
    @GetMapping("/online")
    public ResponseEntity<List<DoctorResponse>> getOnlineDoctors() {
        List<DoctorResponse> doctors = doctorService.getOnlineDoctors();
        return ResponseEntity.ok(doctors);
    }

    /**
     * Update doctor rating
     * POST /api/doctors/{id}/rate
     */
    @PostMapping("/{id}/rate")
    public ResponseEntity<DoctorResponse> updateDoctorRating(
            @PathVariable Long id,
            @RequestParam Integer rating) {
        DoctorResponse doctor = doctorService.updateDoctorRating(id, rating);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Activate doctor
     * POST /api/doctors/{id}/activate
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<DoctorResponse> activateDoctor(@PathVariable Long id) {
        DoctorResponse doctor = doctorService.activateDoctor(id);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Deactivate doctor
     * POST /api/doctors/{id}/deactivate
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<DoctorResponse> deactivateDoctor(@PathVariable Long id) {
        DoctorResponse doctor = doctorService.deactivateDoctor(id);
        return ResponseEntity.ok(doctor);
    }
}
