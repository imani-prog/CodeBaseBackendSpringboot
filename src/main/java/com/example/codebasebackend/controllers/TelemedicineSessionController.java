package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.*;
import com.example.codebasebackend.dto.*;
import com.example.codebasebackend.services.TelemedicineSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/telemedicine/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TelemedicineSessionController {

    private final TelemedicineSessionService sessionService;


    @PostMapping
    public ResponseEntity<TelemedicineSessionResponse> createSession(
            @Valid @RequestBody TelemedicineSessionRequest request) {
        TelemedicineSessionResponse response = sessionService.createSession(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<Page<TelemedicineSessionResponse>> getAllSessions(
            @PageableDefault(size = 10, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TelemedicineSessionResponse> sessions = sessionService.getAllSessions(pageable);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TelemedicineSessionResponse> getSessionById(@PathVariable Long id) {
        TelemedicineSessionResponse session = sessionService.getSessionById(id);
        return ResponseEntity.ok(session);
    }


    @GetMapping("/by-session-id/{sessionId}")
    public ResponseEntity<TelemedicineSessionResponse> getSessionBySessionId(@PathVariable String sessionId) {
        TelemedicineSessionResponse session = sessionService.getSessionBySessionId(sessionId);
        return ResponseEntity.ok(session);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TelemedicineSessionResponse> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody TelemedicineSessionRequest request) {
        TelemedicineSessionResponse session = sessionService.updateSession(id, request);
        return ResponseEntity.ok(session);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/start")
    public ResponseEntity<TelemedicineSessionResponse> startSession(@PathVariable Long id) {
        TelemedicineSessionResponse session = sessionService.startSession(id);
        return ResponseEntity.ok(session);
    }


    @PostMapping("/{id}/pause")
    public ResponseEntity<TelemedicineSessionResponse> pauseSession(@PathVariable Long id) {
        TelemedicineSessionResponse session = sessionService.pauseSession(id);
        return ResponseEntity.ok(session);
    }


    @PostMapping("/{id}/resume")
    public ResponseEntity<TelemedicineSessionResponse> resumeSession(@PathVariable Long id) {
        TelemedicineSessionResponse session = sessionService.resumeSession(id);
        return ResponseEntity.ok(session);
    }


    @PostMapping("/{id}/complete")
    public ResponseEntity<TelemedicineSessionResponse> completeSession(
            @PathVariable Long id,
            @RequestParam String diagnosis,
            @RequestParam(required = false) String prescription,
            @RequestParam(required = false) String doctorNotes) {
        TelemedicineSessionResponse session = sessionService.completeSession(id, diagnosis, prescription, doctorNotes);
        return ResponseEntity.ok(session);
    }


    @PostMapping("/{id}/cancel")
    public ResponseEntity<TelemedicineSessionResponse> cancelSession(
            @PathVariable Long id,
            @RequestParam String reason) {
        TelemedicineSessionResponse session = sessionService.cancelSession(id, reason);
        return ResponseEntity.ok(session);
    }


    @PostMapping("/{id}/terminate")
    public ResponseEntity<TelemedicineSessionResponse> terminateSession(
            @PathVariable Long id,
            @RequestParam String reason) {
        TelemedicineSessionResponse session = sessionService.terminateSession(id, reason);
        return ResponseEntity.ok(session);
    }


    @PostMapping("/{id}/rate")
    public ResponseEntity<TelemedicineSessionResponse> rateSession(
            @PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam(required = false) String feedback) {
        TelemedicineSessionResponse session = sessionService.rateSession(id, rating, feedback);
        return ResponseEntity.ok(session);
    }


    @GetMapping("/by-status/{status}")
    public ResponseEntity<Page<TelemedicineSessionResponse>> getSessionsByStatus(
            @PathVariable SessionStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<TelemedicineSessionResponse> sessions = sessionService.getSessionsByStatus(status, pageable);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/by-platform/{platform}")
    public ResponseEntity<Page<TelemedicineSessionResponse>> getSessionsByPlatform(
            @PathVariable PlatformType platform,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<TelemedicineSessionResponse> sessions = sessionService.getSessionsByPlatform(platform, pageable);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/by-priority/{priority}")
    public ResponseEntity<List<TelemedicineSessionResponse>> getActiveSessionsByPriority(
            @PathVariable Priority priority) {
        List<TelemedicineSessionResponse> sessions = sessionService.getActiveSessionsByPriority(priority);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<TelemedicineSessionResponse>> searchSessions(
            @RequestParam String term,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<TelemedicineSessionResponse> sessions = sessionService.searchSessions(term, pageable);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/filter")
    public ResponseEntity<Page<TelemedicineSessionResponse>> getSessionsWithFilters(
            @RequestParam(required = false) SessionStatus status,
            @RequestParam(required = false) PlatformType platform,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<TelemedicineSessionResponse> sessions = sessionService.getSessionsWithFilters(
            status, platform, priority, doctorId, startDate, endDate, pageable);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<Page<TelemedicineSessionResponse>> getSessionsByPatient(
            @PathVariable Long patientId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<TelemedicineSessionResponse> sessions = sessionService.getSessionsByPatient(patientId, pageable);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/by-doctor/{doctorId}")
    public ResponseEntity<Page<TelemedicineSessionResponse>> getSessionsByDoctor(
            @PathVariable Long doctorId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<TelemedicineSessionResponse> sessions = sessionService.getSessionsByDoctor(doctorId, pageable);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/doctor/{doctorId}/active")
    public ResponseEntity<List<TelemedicineSessionResponse>> getActiveDoctorSessions(
            @PathVariable Long doctorId) {
        List<TelemedicineSessionResponse> sessions = sessionService.getActiveDoctorSessions(doctorId);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/overview")
    public ResponseEntity<PlatformOverviewResponse> getPlatformOverview() {
        PlatformOverviewResponse overview = sessionService.getPlatformOverview();
        return ResponseEntity.ok(overview);
    }


    @GetMapping("/revenue")
    public ResponseEntity<RevenueDataResponse> getRevenueData(
            @RequestParam(defaultValue = "monthly") String period) {
        RevenueDataResponse revenue = sessionService.getRevenueData(period);
        return ResponseEntity.ok(revenue);
    }


    @GetMapping("/platform-stats")
    public ResponseEntity<PlatformStatsResponse> getPlatformStats() {
        PlatformStatsResponse stats = sessionService.getPlatformStats();
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/doctors/online")
    public ResponseEntity<List<DoctorOnlineResponse>> getOnlineDoctors() {
        List<DoctorOnlineResponse> doctors = sessionService.getOnlineDoctors();
        return ResponseEntity.ok(doctors);
    }


    @GetMapping("/history")
    public ResponseEntity<List<SessionHistoryResponse>> getSessionHistory(
            @RequestParam(defaultValue = "today") String period) {
        List<SessionHistoryResponse> history = sessionService.getSessionHistory(period);
        return ResponseEntity.ok(history);
    }


    @PostMapping("/send-reminders")
    public ResponseEntity<Void> sendSessionReminders() {
        sessionService.sendSessionReminders();
        return ResponseEntity.ok().build();
    }
}
