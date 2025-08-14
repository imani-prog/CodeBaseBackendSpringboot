package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.ReportRequest;
import com.example.codebasebackend.dto.ReportResponse;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReportService {
    ReportResponse create(ReportRequest request);
    ReportResponse get(Long id);
    List<ReportResponse> list(String type, String status, Long hospitalId, Long userId,
                               OffsetDateTime from, OffsetDateTime to);
    ReportResponse update(Long id, ReportRequest request);
    ReportResponse updateStatus(Long id, String status, String errorMessage);
    ReportResponse setFileUrl(Long id, String fileUrl);
    void delete(Long id);
}
