package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.dto.HealthRecordRequest;
import com.example.codebasebackend.dto.HealthRecordResponse;
import com.example.codebasebackend.services.HealthRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
public class HealthRecordController {

	private final HealthRecordService healthRecordService;

	@PostMapping
	@Auditable(eventType = AuditLog.EventType.CREATE, entityType = "HealthRecord", entityIdExpression = "#result.body.id", includeArgs = true)
	public ResponseEntity<HealthRecordResponse> create(@Valid @RequestBody HealthRecordRequest request) {
		return ResponseEntity.ok(healthRecordService.create(request));
	}

	@GetMapping("/{id}")
	@Auditable(eventType = AuditLog.EventType.READ, entityType = "HealthRecord", entityIdExpression = "#id", includeArgs = true)
	public ResponseEntity<HealthRecordResponse> get(@PathVariable Long id) {
		return ResponseEntity.ok(healthRecordService.get(id));
	}

	@PutMapping("/{id}")
	@Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "HealthRecord", entityIdExpression = "#id", includeArgs = true)
	public ResponseEntity<HealthRecordResponse> update(@PathVariable Long id,
													   @Valid @RequestBody HealthRecordRequest request) {
		return ResponseEntity.ok(healthRecordService.update(id, request));
	}

	@DeleteMapping("/{id}")
	@Auditable(eventType = AuditLog.EventType.DELETE, entityType = "HealthRecord", entityIdExpression = "#id", includeArgs = true)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		healthRecordService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	@Auditable(eventType = AuditLog.EventType.READ, entityType = "HealthRecord", includeArgs = true)
	public ResponseEntity<Page<HealthRecordResponse>> search(
			@RequestParam(required = false) Long patientId,
			@RequestParam(required = false) String recordType,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String searchTerm,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		return ResponseEntity.ok(
				healthRecordService.search(patientId, recordType, status, searchTerm, fromDate, toDate, page, size)
		);
	}

	@GetMapping("/patient/{patientId}")
	@Auditable(eventType = AuditLog.EventType.READ, entityType = "HealthRecord", includeArgs = true)
	public ResponseEntity<List<HealthRecordResponse>> listByPatient(@PathVariable Long patientId) {
		return ResponseEntity.ok(healthRecordService.listByPatient(patientId));
	}
}
