package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.HealthRecordRequest;
import com.example.codebasebackend.dto.HealthRecordResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface HealthRecordService {
	HealthRecordResponse create(HealthRecordRequest request);
	HealthRecordResponse update(Long id, HealthRecordRequest request);
	HealthRecordResponse get(Long id);
	void delete(Long id);

	Page<HealthRecordResponse> search(Long patientId, String recordType, String status, String searchTerm,
									  LocalDate fromDate, LocalDate toDate, int page, int size);

	List<HealthRecordResponse> listByPatient(Long patientId);
}
