package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.dto.HealthRecordRequest;
import com.example.codebasebackend.dto.HealthRecordResponse;
import com.example.codebasebackend.repositories.PatientRepository;
import com.example.codebasebackend.services.HealthRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
public class HealthRecordController {

	private final HealthRecordService healthRecordService;
	private final PatientRepository patientRepository;

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
	@PreAuthorize("hasAnyRole('ADMIN','CHW','PATIENT')")
	public ResponseEntity<Page<HealthRecordResponse>> search(
			@RequestParam(required = false) Long patientId,
			@RequestParam(required = false) String recordType,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String searchTerm,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Authentication authentication) {

		Long scopedPatientId = enforcePatientScope(patientId, authentication);

		return ResponseEntity.ok(
				healthRecordService.search(scopedPatientId, recordType, status, searchTerm, fromDate, toDate, page, size)
		);
	}

	@GetMapping("/patient/{patientId}")
	@Auditable(eventType = AuditLog.EventType.READ, entityType = "HealthRecord", includeArgs = true)
	@PreAuthorize("hasAnyRole('ADMIN','CHW','PATIENT')")
	public ResponseEntity<List<HealthRecordResponse>> listByPatient(@PathVariable Long patientId,
												Authentication authentication) {
		Long scopedPatientId = enforcePatientScope(patientId, authentication);
		return ResponseEntity.ok(healthRecordService.listByPatient(scopedPatientId));
	}

	private Long enforcePatientScope(Long requestedPatientId, Authentication authentication) {
		if (!hasRole(authentication, "ROLE_PATIENT")) {
			return requestedPatientId;
		}

		Patient currentPatient = patientRepository.findByUserUsername(authentication.getName())
				.orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Patient profile not found for authenticated user"));

		if (requestedPatientId == null) {
			return currentPatient.getId();
		}

		if (!currentPatient.getId().equals(requestedPatientId)) {
			throw new ResponseStatusException(FORBIDDEN, "You can only access your own health records");
		}

		return requestedPatientId;
	}

	private boolean hasRole(Authentication authentication, String role) {
		if (authentication == null || authentication.getAuthorities() == null) {
			return false;
		}
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			if (role.equals(authority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
}
