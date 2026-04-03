package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.CommunityHealthWorkerAssignment;
import com.example.codebasebackend.dto.ChwAssignmentReassignRequest;
import com.example.codebasebackend.dto.ChwAssignmentRequest;
import com.example.codebasebackend.dto.ChwAssignmentResponse;
import com.example.codebasebackend.dto.ChwAssignmentStatusUpdateRequest;
import com.example.codebasebackend.services.CommunityHealthWorkerAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class CommunityHealthWorkerAssignmentController {

    private final CommunityHealthWorkerAssignmentService assignmentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<List<ChwAssignmentResponse>> list(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long chwId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assignmentType) {

        CommunityHealthWorkerAssignment.Status statusEnum = null;
        if (status != null && !status.isBlank()) {
            statusEnum = CommunityHealthWorkerAssignment.Status.valueOf(status.toUpperCase());
        }

        CommunityHealthWorkerAssignment.AssignmentType typeEnum = null;
        if (assignmentType != null && !assignmentType.isBlank()) {
            typeEnum = CommunityHealthWorkerAssignment.AssignmentType.valueOf(assignmentType.toUpperCase());
        }

        return ResponseEntity.ok(assignmentService.list(patientId, chwId, statusEnum, typeEnum));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<ChwAssignmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<ChwAssignmentResponse> create(@Valid @RequestBody ChwAssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.create(request));
    }

    @PostMapping("/home-visits")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<ChwAssignmentResponse> createHomeVisitAssignment(@Valid @RequestBody ChwAssignmentRequest request) {
        request.setAssignmentType(CommunityHealthWorkerAssignment.AssignmentType.HOME_VISIT.name());
        return ResponseEntity.ok(assignmentService.create(request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<ChwAssignmentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChwAssignmentStatusUpdateRequest request) {

        CommunityHealthWorkerAssignment.Status status = CommunityHealthWorkerAssignment.Status.valueOf(request.getStatus().toUpperCase());
        return ResponseEntity.ok(assignmentService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/reassign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChwAssignmentResponse> reassign(
            @PathVariable Long id,
            @Valid @RequestBody ChwAssignmentReassignRequest request) {

        return ResponseEntity.ok(assignmentService.reassign(id, request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChwAssignmentResponse> patch(@PathVariable Long id, @Valid @RequestBody ChwAssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.replace(id, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChwAssignmentResponse> put(@PathVariable Long id, @Valid @RequestBody ChwAssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.replace(id, request));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW') or hasRole('PATIENT')")
    public ResponseEntity<List<ChwAssignmentResponse>> byPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(assignmentService.listByPatient(patientId));
    }

    @GetMapping("/chw/{chwId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<List<ChwAssignmentResponse>> byChw(@PathVariable Long chwId) {
        return ResponseEntity.ok(assignmentService.listByChw(chwId));
    }
}

