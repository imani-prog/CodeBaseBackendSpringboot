package com.example.codebasebackend.configs;

import com.example.codebasebackend.services.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditRetentionJob {

    private final AuditService auditService;

    @Value("${app.audit.retention-days:365}")
    private int retentionDays;

    // Run daily at 03:00 server time
    @Scheduled(cron = "0 0 3 * * *")
    public void purgeOldLogs() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(retentionDays);
        long deleted = auditService.purgeBefore(cutoff);
        log.info("Audit retention purge complete: {} records deleted (before {}).", deleted, cutoff);
    }
}

