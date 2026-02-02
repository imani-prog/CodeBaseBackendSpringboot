package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.Appointment;
import com.example.codebasebackend.dto.AppointmentRequest;
import com.example.codebasebackend.dto.AppointmentResponse;
import com.example.codebasebackend.dto.CancelRequest;
import com.example.codebasebackend.dto.RescheduleRequest;
import com.example.codebasebackend.services.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.create(request));

    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> byPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.listByPatient(patientId));
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<AppointmentResponse>> byHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(appointmentService.listByHospital(hospitalId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentResponse>> byStatus(@PathVariable String status) {
        return ResponseEntity.ok(appointmentService.listByStatus(Appointment.AppointmentStatus.valueOf(status.toUpperCase())));
    }

    @GetMapping("/range")
    public ResponseEntity<List<AppointmentResponse>> inRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return ResponseEntity.ok(appointmentService.listInRange(from, to));
    }


    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAll() {
        return ResponseEntity.ok(appointmentService.listAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AppointmentResponse>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
            appointmentService.searchAppointments(status, type, searchTerm, page, size)
        );
    }


    @PatchMapping("/{id}/check-in")
    public ResponseEntity<AppointmentResponse> checkIn(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.checkIn(id));
    }


    @PatchMapping("/{id}/check-out")
    public ResponseEntity<AppointmentResponse> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.checkOut(id));
    }


    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(
            @PathVariable Long id,
            @RequestBody(required = false) CancelRequest request) {
        String reason = request != null ? request.getReason() : null;
        return ResponseEntity.ok(appointmentService.cancel(id, reason));
    }

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentResponse> reschedule(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleRequest request) {
        return ResponseEntity.ok(
            appointmentService.reschedule(
                id,
                request.getNewStart(),
                request.getNewEnd()
            )
        );
    }


    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.confirm(id));
    }
}
