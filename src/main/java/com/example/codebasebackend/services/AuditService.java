package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.AuditLogRequest;
import com.example.codebasebackend.dto.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;

public interface AuditService {
    AuditLogResponse log(AuditLogRequest request);
    AuditLogResponse get(Long id);
    Page<AuditLogResponse> search(String eventType,
                                  String entityType,
                                  String entityId,
                                  Long userId,
                                  String username,
                                  String status,
                                  Long integrationPartnerId,
                                  OffsetDateTime from,
                                  OffsetDateTime to,
                                  Pageable pageable);
    long purgeBefore(OffsetDateTime before);
}
