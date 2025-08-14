package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.Entities.Reports;
import com.example.codebasebackend.Entities.User;
import com.example.codebasebackend.dto.ReportRequest;
import com.example.codebasebackend.dto.ReportResponse;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.ReportRepository;
import com.example.codebasebackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImplementation implements ReportService {

    private final ReportRepository reportRepo;
    private final UserRepository userRepo;
    private final HospitalRepository hospitalRepo;

    @Override
    public ReportResponse create(ReportRequest r) {
        Reports e = new Reports();
        e.setType(parseType(r.getType()));
        e.setTitle(r.getTitle());
        e.setDescription(r.getDescription());
        if (r.getGeneratedByUserId() != null) e.setGeneratedBy(findUser(r.getGeneratedByUserId()));
        if (r.getHospitalId() != null) e.setHospital(findHospital(r.getHospitalId()));
        e.setPeriodStart(r.getPeriodStart());
        e.setPeriodEnd(r.getPeriodEnd());
        e.setParameters(r.getParameters());
        e.setRelatedEntities(r.getRelatedEntities());
        e.setStatus(Reports.ReportStatus.PENDING);
        Reports saved = reportRepo.save(e);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponse get(Long id) {
        return reportRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Report not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> list(String type, String status, Long hospitalId, Long userId,
                                     OffsetDateTime from, OffsetDateTime to) {
        List<Reports> all = reportRepo.findAll();
        return all.stream()
                .filter(e -> type == null || e.getType() == parseType(type))
                .filter(e -> status == null || e.getStatus() == parseStatus(status))
                .filter(e -> hospitalId == null || (e.getHospital() != null && Objects.equals(e.getHospital().getId(), hospitalId)))
                .filter(e -> userId == null || (e.getGeneratedBy() != null && Objects.equals(e.getGeneratedBy().getId(), userId)))
                .filter(e -> from == null || (e.getCreatedAt() != null && !e.getCreatedAt().isBefore(from)))
                .filter(e -> to == null || (e.getCreatedAt() != null && !e.getCreatedAt().isAfter(to)))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReportResponse update(Long id, ReportRequest r) {
        Reports e = reportRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Report not found"));
        if (r.getType() != null) e.setType(parseType(r.getType()));
        if (r.getTitle() != null) e.setTitle(r.getTitle());
        if (r.getDescription() != null) e.setDescription(r.getDescription());
        if (r.getGeneratedByUserId() != null) e.setGeneratedBy(findUser(r.getGeneratedByUserId()));
        if (r.getHospitalId() != null) e.setHospital(findHospital(r.getHospitalId()));
        if (r.getPeriodStart() != null) e.setPeriodStart(r.getPeriodStart());
        if (r.getPeriodEnd() != null) e.setPeriodEnd(r.getPeriodEnd());
        if (r.getParameters() != null) e.setParameters(r.getParameters());
        if (r.getRelatedEntities() != null) e.setRelatedEntities(r.getRelatedEntities());
        return toResponse(reportRepo.save(e));
    }

    @Override
    public ReportResponse updateStatus(Long id, String status, String errorMessage) {
        Reports e = reportRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Report not found"));
        e.setStatus(parseStatus(status));
        if (errorMessage != null) e.setErrorMessage(errorMessage);
        return toResponse(reportRepo.save(e));
    }

    @Override
    public ReportResponse setFileUrl(Long id, String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) throw new ResponseStatusException(BAD_REQUEST, "fileUrl required");
        Reports e = reportRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Report not found"));
        e.setFileUrl(fileUrl);
        return toResponse(reportRepo.save(e));
    }

    @Override
    public void delete(Long id) {
        if (!reportRepo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Report not found");
        reportRepo.deleteById(id);
    }

    private Reports.ReportType parseType(String s) {
        if (s == null) throw new ResponseStatusException(BAD_REQUEST, "type required");
        try { return Reports.ReportType.valueOf(s.trim().toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid report type"); }
    }

    private Reports.ReportStatus parseStatus(String s) {
        if (s == null) throw new ResponseStatusException(BAD_REQUEST, "status required");
        try { return Reports.ReportStatus.valueOf(s.trim().toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid report status"); }
    }

    private User findUser(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    private Hospital findHospital(Long id) {
        return hospitalRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
    }

    private ReportResponse toResponse(Reports e) {
        ReportResponse dto = new ReportResponse();
        dto.setId(e.getId());
        dto.setType(e.getType() != null ? e.getType().name() : null);
        dto.setTitle(e.getTitle());
        dto.setDescription(e.getDescription());
        dto.setGeneratedByUserId(e.getGeneratedBy() != null ? e.getGeneratedBy().getId() : null);
        dto.setHospitalId(e.getHospital() != null ? e.getHospital().getId() : null);
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        dto.setPeriodStart(e.getPeriodStart());
        dto.setPeriodEnd(e.getPeriodEnd());
        dto.setParameters(e.getParameters());
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setFileUrl(e.getFileUrl());
        dto.setRawData(e.getRawData());
        dto.setErrorMessage(e.getErrorMessage());
        dto.setRelatedEntities(e.getRelatedEntities());
        return dto;
    }
}
