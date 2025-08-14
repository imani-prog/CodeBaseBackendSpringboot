package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.OffsetDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {
    long deleteByEventTimeBefore(OffsetDateTime cutoff);
}

