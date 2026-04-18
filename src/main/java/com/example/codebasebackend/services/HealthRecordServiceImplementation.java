package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Doctor;
import com.example.codebasebackend.Entities.HealthRecord;
import com.example.codebasebackend.Entities.HealthRecordAttachment;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.dto.HealthRecordAttachmentDto;
import com.example.codebasebackend.dto.HealthRecordRequest;
import com.example.codebasebackend.dto.HealthRecordResponse;
import com.example.codebasebackend.repositories.DoctorRepository;
import com.example.codebasebackend.repositories.HealthRecordRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class HealthRecordServiceImplementation implements HealthRecordService {

	private final HealthRecordRepository healthRecordRepository;
	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;
	private final HospitalRepository hospitalRepository;

	@Override
	public HealthRecordResponse create(HealthRecordRequest request) {
		Patient patient = loadPatient(request.getPatientId());

		HealthRecord record = new HealthRecord();
		record.setPatient(patient);
		applyRelations(record, request);
		applyPayload(record, request);
		record.setRecordCode(generateCode());

		HealthRecord saved = healthRecordRepository.save(record);
		return toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public HealthRecordResponse get(Long id) {
		return healthRecordRepository.findById(id)
				.map(this::toResponse)
				.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Health record not found"));
	}

	@Override
	public HealthRecordResponse update(Long id, HealthRecordRequest request) {
		HealthRecord record = healthRecordRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Health record not found"));

		if (!record.getPatient().getId().equals(request.getPatientId())) {
			record.setPatient(loadPatient(request.getPatientId()));
		}

		applyRelations(record, request);
		applyPayload(record, request);

		HealthRecord saved = healthRecordRepository.save(record);
		return toResponse(saved);
	}

	@Override
	public void delete(Long id) {
		if (!healthRecordRepository.existsById(id)) {
			throw new ResponseStatusException(NOT_FOUND, "Health record not found");
		}
		healthRecordRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<HealthRecordResponse> search(Long patientId, String recordType, String status, String searchTerm,
											 LocalDate fromDate, LocalDate toDate, int page, int size) {
		if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
			throw new ResponseStatusException(BAD_REQUEST, "toDate must be on or after fromDate");
		}

		List<HealthRecord.RecordType> types = mapTypes(recordType);
		List<HealthRecord.RecordStatus> statuses = mapStatuses(status);
		String search = (searchTerm != null && !searchTerm.isBlank()) ? searchTerm : null;
		Pageable pageable = PageRequest.of(page, size, Sort.by("visitDate").descending().and(Sort.by("createdAt").descending()));

		List<HealthRecord.RecordType> effectiveTypes =
				types != null ? types : Arrays.asList(HealthRecord.RecordType.values());
		List<HealthRecord.RecordStatus> effectiveStatuses =
				statuses != null ? statuses : Arrays.asList(HealthRecord.RecordStatus.values());

		Specification<HealthRecord> spec = (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (patientId != null) {
				predicates.add(cb.equal(root.get("patient").get("id"), patientId));
			}
			predicates.add(root.get("recordType").in(effectiveTypes));
			predicates.add(root.get("status").in(effectiveStatuses));
			if (fromDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("visitDate"), fromDate));
			}
			if (toDate != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("visitDate"), toDate));
			}
			if (search != null) {
				String pattern = "%" + search.toLowerCase() + "%";
				predicates.add(cb.or(
						cb.like(cb.lower(cb.coalesce(root.get("summary"), "")), pattern),
						cb.like(cb.lower(cb.coalesce(root.get("notes"), "")), pattern),
						cb.like(cb.lower(cb.coalesce(root.get("providerName"), "")), pattern),
						cb.like(cb.lower(cb.coalesce(root.get("diagnosis"), "")), pattern)
				));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};

		return healthRecordRepository.findAll(spec, pageable)
				.map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public List<HealthRecordResponse> listByPatient(Long patientId) {
		return healthRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId)
				.stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	private void applyPayload(HealthRecord record, HealthRecordRequest request) {
		record.setRecordType(parseType(request.getRecordType()));
		if (request.getStatus() != null && !request.getStatus().isBlank()) {
			record.setStatus(parseStatus(request.getStatus()));
		}
		record.setVisitDate(request.getVisitDate());
		record.setDueDate(request.getDueDate());
		record.setProviderName(request.getProviderName());
		record.setProviderSpecialty(request.getProviderSpecialty());
		record.setSummary(request.getSummary());
		record.setNotes(request.getNotes());
		record.setDiagnosis(request.getDiagnosis());
		record.setVaccineName(request.getVaccineName());
		record.setMedicationName(request.getMedicationName());
		record.setDosage(request.getDosage());
		record.setFrequency(request.getFrequency());
		record.setDurationText(request.getDurationText());
		record.setRefillsRemaining(request.getRefillsRemaining());
		record.setTotalRefills(request.getTotalRefills());

		List<HealthRecordAttachment> attachmentEntities = new ArrayList<>();
		if (request.getAttachments() != null) {
			for (HealthRecordAttachmentDto dto : request.getAttachments()) {
				HealthRecordAttachment attachment = new HealthRecordAttachment();
				attachment.setFileName(dto.getFileName());
				attachment.setFileUrl(dto.getFileUrl());
				attachment.setMimeType(dto.getMimeType());
				attachment.setSizeBytes(dto.getSizeBytes());
				attachment.setCategory(dto.getCategory());
				attachment.setUploadedBy(dto.getUploadedBy());
				attachment.setUploadDate(dto.getUploadDate());
				attachmentEntities.add(attachment);
			}
		}
		record.setAttachments(attachmentEntities);
	}

	private void applyRelations(HealthRecord record, HealthRecordRequest request) {
		if (request.getDoctorId() != null) {
			Doctor doctor = doctorRepository.findById(request.getDoctorId())
					.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Doctor not found"));
			record.setDoctor(doctor);
		} else {
			record.setDoctor(null);
		}

		if (request.getHospitalId() != null) {
			Hospital hospital = hospitalRepository.findById(request.getHospitalId())
					.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
			record.setHospital(hospital);
		} else {
			record.setHospital(null);
		}
	}

	private Patient loadPatient(Long patientId) {
		return patientRepository.findById(patientId)
				.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
	}

	private HealthRecord.RecordType parseType(String type) {
		try {
			return HealthRecord.RecordType.valueOf(type.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			throw new ResponseStatusException(BAD_REQUEST, "Invalid record type");
		}
	}

	private List<HealthRecord.RecordType> mapTypes(String type) {
		if (type == null || type.isBlank()) return null;
		return List.of(parseType(type));
	}

	private HealthRecord.RecordStatus parseStatus(String status) {
		try {
			return HealthRecord.RecordStatus.valueOf(status.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			throw new ResponseStatusException(BAD_REQUEST, "Invalid record status");
		}
	}

	private List<HealthRecord.RecordStatus> mapStatuses(String status) {
		if (status == null || status.isBlank()) return null;
		if ("active".equalsIgnoreCase(status)) {
			return List.of(HealthRecord.RecordStatus.ACTIVE, HealthRecord.RecordStatus.UPCOMING);
		}
		if ("completed".equalsIgnoreCase(status)) {
			return List.of(HealthRecord.RecordStatus.COMPLETED, HealthRecord.RecordStatus.ARCHIVED);
		}
		return List.of(parseStatus(status));
	}

	private String generateCode() {
		return "HR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
	}

	private HealthRecordResponse toResponse(HealthRecord hr) {
		HealthRecordResponse dto = new HealthRecordResponse();
		dto.setId(hr.getId());
		dto.setRecordCode(hr.getRecordCode());
		dto.setRecordType(hr.getRecordType() != null ? hr.getRecordType().name() : null);
		dto.setStatus(hr.getStatus() != null ? hr.getStatus().name() : null);
		dto.setVisitDate(hr.getVisitDate());
		dto.setDueDate(hr.getDueDate());
		dto.setProviderName(hr.getProviderName());
		dto.setProviderSpecialty(hr.getProviderSpecialty());
		dto.setSummary(hr.getSummary());
		dto.setNotes(hr.getNotes());
		dto.setDiagnosis(hr.getDiagnosis());
		dto.setVaccineName(hr.getVaccineName());
		dto.setMedicationName(hr.getMedicationName());
		dto.setDosage(hr.getDosage());
		dto.setFrequency(hr.getFrequency());
		dto.setDurationText(hr.getDurationText());
		dto.setRefillsRemaining(hr.getRefillsRemaining());
		dto.setTotalRefills(hr.getTotalRefills());

		dto.setPatientId(hr.getPatient() != null ? hr.getPatient().getId() : null);
		if (hr.getPatient() != null) {
			dto.setPatientName(buildFullName(hr.getPatient().getFirstName(), hr.getPatient().getMiddleName(), hr.getPatient().getLastName()));
		}

		dto.setDoctorId(hr.getDoctor() != null ? hr.getDoctor().getId() : null);
		dto.setDoctorName(hr.getDoctor() != null ? hr.getDoctor().getFullName() : null);

		dto.setHospitalId(hr.getHospital() != null ? hr.getHospital().getId() : null);
		dto.setHospitalName(hr.getHospital() != null ? hr.getHospital().getName() : null);

		List<HealthRecordAttachmentDto> attachmentDtos = hr.getAttachments() != null
				? hr.getAttachments().stream().map(this::toAttachmentDto).collect(Collectors.toList())
				: new ArrayList<>();
		dto.setAttachments(attachmentDtos);

		dto.setCreatedAt(hr.getCreatedAt());
		dto.setUpdatedAt(hr.getUpdatedAt());
		return dto;
	}

	private HealthRecordAttachmentDto toAttachmentDto(HealthRecordAttachment attachment) {
		HealthRecordAttachmentDto dto = new HealthRecordAttachmentDto();
		dto.setFileName(attachment.getFileName());
		dto.setFileUrl(attachment.getFileUrl());
		dto.setMimeType(attachment.getMimeType());
		dto.setSizeBytes(attachment.getSizeBytes());
		dto.setCategory(attachment.getCategory());
		dto.setUploadedBy(attachment.getUploadedBy());
		dto.setUploadDate(attachment.getUploadDate());
		return dto;
	}

	private String buildFullName(String firstName, String middleName, String lastName) {
		StringBuilder sb = new StringBuilder();
		if (firstName != null && !firstName.isBlank()) sb.append(firstName);
		if (middleName != null && !middleName.isBlank()) {
			if (sb.length() > 0) sb.append(" ");
			sb.append(middleName);
		}
		if (lastName != null && !lastName.isBlank()) {
			if (sb.length() > 0) sb.append(" ");
			sb.append(lastName);
		}
		return sb.toString();
	}
}
