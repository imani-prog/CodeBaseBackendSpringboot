package com.example.codebasebackend.controllers;

import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.dto.*;
import com.example.codebasebackend.services.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "Report", entityIdExpression = "#result.id", includeArgs = true)
    @PostMapping
    public ResponseEntity<ReportResponse> create(@Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.create(request));
    }

    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Report", entityIdExpression = "#id")
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.get(id));
    }

    @Auditable(eventType = AuditLog.EventType.READ, entityType = "Report")
    @GetMapping
    public ResponseEntity<List<ReportResponse>> list(@RequestParam(required = false) String type,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(required = false) Long hospitalId,
                                                     @RequestParam(required = false, name = "userId") Long generatedByUserId,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return ResponseEntity.ok(reportService.list(type, status, hospitalId, generatedByUserId, from, to));
    }

    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "Report", entityIdExpression = "#id", includeArgs = true)
    @PutMapping("/{id}")
    public ResponseEntity<ReportResponse> update(@PathVariable Long id, @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.update(id, request));
    }

    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "Report", entityIdExpression = "#id", includeArgs = true)
    @PatchMapping("/{id}/status")
    public ResponseEntity<ReportResponse> updateStatus(@PathVariable Long id,
                                                       @Valid @RequestBody ReportStatusUpdateRequest request) {
        return ResponseEntity.ok(reportService.updateStatus(id, request.getStatus(), request.getErrorMessage()));
    }

    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "Report", entityIdExpression = "#id", includeArgs = true)
    @PatchMapping("/{id}/file")
    public ResponseEntity<ReportResponse> updateFile(@PathVariable Long id,
                                                     @Valid @RequestBody ReportFileUpdateRequest request) {
        return ResponseEntity.ok(reportService.setFileUrl(id, request.getFileUrl()));
    }

    @Auditable(eventType = AuditLog.EventType.DELETE, entityType = "Report", entityIdExpression = "#id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
