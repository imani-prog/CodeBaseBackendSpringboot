package com.example.codebasebackend.configs;

import com.example.codebasebackend.Entities.AuditLog;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    AuditLog.EventType eventType();
    String entityType() default ""; // e.g., "Patient"
    String entityIdExpression() default ""; // SpEL, e.g., "#id" or "#result.id"
    boolean includeArgs() default false; // serialize method args into details (redacted/truncated)
    boolean includeResult() default false; // serialize result into details (redacted/truncated)
}

