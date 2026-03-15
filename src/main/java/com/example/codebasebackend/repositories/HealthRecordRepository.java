package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.HealthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
	List<HealthRecord> findByPatientIdOrderByVisitDateDesc(Long patientId);

	Page<HealthRecord> findByPatientId(Long patientId, Pageable pageable);

	@Query("SELECT hr FROM HealthRecord hr WHERE " +
		   "(:patientId IS NULL OR hr.patient.id = :patientId) AND " +
		   "(:types IS NULL OR hr.recordType IN :types) AND " +
		   "(:statuses IS NULL OR hr.status IN :statuses) AND " +
		   "(:fromDate IS NULL OR hr.visitDate >= :fromDate) AND " +
		   "(:toDate IS NULL OR hr.visitDate <= :toDate) AND " +
		   "(:searchTerm IS NULL OR " +
		   "  LOWER(hr.summary) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
		   "  LOWER(hr.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
		   "  LOWER(hr.providerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
		   "  LOWER(hr.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
		   "ORDER BY hr.visitDate DESC NULLS LAST, hr.createdAt DESC")
	Page<HealthRecord> search(@Param("patientId") Long patientId,
							  @Param("types") Collection<HealthRecord.RecordType> types,
							  @Param("statuses") Collection<HealthRecord.RecordStatus> statuses,
							  @Param("fromDate") LocalDate fromDate,
							  @Param("toDate") LocalDate toDate,
							  @Param("searchTerm") String searchTerm,
							  Pageable pageable);
}
