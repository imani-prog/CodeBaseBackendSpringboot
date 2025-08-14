package com.example.codebasebackend.controllers;

import com.example.codebasebackend.dto.AuditLogRequest;
import com.example.codebasebackend.dto.AuditLogResponse;
import com.example.codebasebackend.services.AuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditService auditService;

    @PostMapping
    public ResponseEntity<AuditLogResponse> create(@Valid @RequestBody AuditLogRequest request) {
        return ResponseEntity.ok(auditService.log(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<AuditLogResponse>> search(@RequestParam(required = false) String eventType,
                                                         @RequestParam(required = false) String entityType,
                                                         @RequestParam(required = false) String entityId,
                                                         @RequestParam(required = false) Long userId,
                                                         @RequestParam(required = false) String username,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) Long integrationPartnerId,
                                                         @RequestParam(required = false)
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
                                                         @RequestParam(required = false)
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
                                                         Pageable pageable) {
        return ResponseEntity.ok(auditService.search(eventType, entityType, entityId, userId, username, status,
                integrationPartnerId, from, to, pageable));
    }

    @DeleteMapping("/purge")
    public ResponseEntity<Long> purge(@RequestParam
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime before) {
        return ResponseEntity.ok(auditService.purgeBefore(before));
    }
}
