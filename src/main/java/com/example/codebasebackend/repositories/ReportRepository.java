package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Reports;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Reports, Long> {
    List<Reports> findByType(Reports.ReportType type);
    List<Reports> findByStatus(Reports.ReportStatus status);
    List<Reports> findByHospitalId(Long hospitalId);
    List<Reports> findByGeneratedById(Long userId);
    List<Reports> findByCreatedAtBetween(OffsetDateTime from, OffsetDateTime to);
}
