package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.HomeVisit;
import com.example.codebasebackend.dto.HomeVisitCancelRequest;
import com.example.codebasebackend.dto.HomeVisitCompleteRequest;
import com.example.codebasebackend.dto.HomeVisitLocationRequest;
import com.example.codebasebackend.dto.HomeVisitRequest;
import com.example.codebasebackend.dto.HomeVisitResponse;
import com.example.codebasebackend.dto.HomeVisitRescheduleRequest;
import com.example.codebasebackend.services.HomeVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home-visits")
@RequiredArgsConstructor
public class HomeVisitController {

    private final HomeVisitService homeVisitService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<List<HomeVisitResponse>> list(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long chwId,
            @RequestParam(required = false) String status) {

        HomeVisit.Status parsedStatus = null;
        if (status != null && !status.isBlank()) {
            parsedStatus = HomeVisit.Status.valueOf(status.toUpperCase());
        }
        return ResponseEntity.ok(homeVisitService.list(patientId, chwId, parsedStatus));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<HomeVisitResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(homeVisitService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<HomeVisitResponse> create(@RequestBody HomeVisitRequest request) {
        return ResponseEntity.ok(homeVisitService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<HomeVisitResponse> update(@PathVariable Long id, @RequestBody HomeVisitRequest request) {
        return ResponseEntity.ok(homeVisitService.update(id, request));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<HomeVisitResponse> complete(@PathVariable Long id, @RequestBody(required = false) HomeVisitCompleteRequest request) {
        return ResponseEntity.ok(homeVisitService.complete(id, request == null ? new HomeVisitCompleteRequest() : request));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<HomeVisitResponse> cancel(@PathVariable Long id, @RequestBody(required = false) HomeVisitCancelRequest request) {
        return ResponseEntity.ok(homeVisitService.cancel(id, request == null ? new HomeVisitCancelRequest() : request));
    }

    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<HomeVisitResponse> reschedule(@PathVariable Long id, @RequestBody HomeVisitRescheduleRequest request) {
        return ResponseEntity.ok(homeVisitService.reschedule(id, request));
    }

    @PatchMapping("/{id}/location")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHW')")
    public ResponseEntity<HomeVisitResponse> updateLocation(@PathVariable Long id, @RequestBody HomeVisitLocationRequest request) {
        return ResponseEntity.ok(homeVisitService.updateLocation(id, request));
    }
}

