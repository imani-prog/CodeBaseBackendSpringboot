
package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.TrainingEnrollment.EnrollmentStatus;
import com.example.codebasebackend.dto.EnrollmentRequest;
import com.example.codebasebackend.dto.EnrollmentResponse;
import com.example.codebasebackend.services.TrainingEnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-modules")
@RequiredArgsConstructor
public class TrainingEnrollmentController {

    private final TrainingEnrollmentService enrollmentService;

    // Enroll a CHW into a module
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{moduleId}/enrollments")
    public ResponseEntity<EnrollmentResponse> enroll(
            @PathVariable Long moduleId,
            @Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.ok(enrollmentService.enroll(moduleId, request));
    }

    // List all enrollments for a module
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{moduleId}/enrollments")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByModule(
            @PathVariable Long moduleId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByModule(moduleId));
    }

    // List all modules a CHW is enrolled in
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/enrollments/chw/{chwId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByChw(
            @PathVariable Long chwId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByChw(chwId));
    }

    // Update progress
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/enrollments/{enrollmentId}/progress")
    public ResponseEntity<EnrollmentResponse> updateProgress(
            @PathVariable Long enrollmentId,
            @RequestParam int progressPercentage) {
        return ResponseEntity.ok(enrollmentService.updateProgress(enrollmentId, progressPercentage));
    }

    // Update status
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/enrollments/{enrollmentId}/status")
    public ResponseEntity<EnrollmentResponse> updateStatus(
            @PathVariable Long enrollmentId,
            @RequestParam EnrollmentStatus status) {
        return ResponseEntity.ok(enrollmentService.updateStatus(enrollmentId, status));
    }

    // Unenroll
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<Void> unenroll(@PathVariable Long enrollmentId) {
        enrollmentService.unenroll(enrollmentId);
        return ResponseEntity.noContent().build();
    }
}