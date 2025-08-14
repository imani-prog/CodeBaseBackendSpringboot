# AuditLog — Comprehensive Design, Implementation, and Usage Guide

This document explains the AuditLog subsystem implemented in the MediLink backend: its purpose, data model, API, automatic logging via AOP, security/privacy controls, retention, and how to extend it safely.

## 1) Purpose
- Security: Trace sensitive actions (logins, reads/writes, config and permission changes).
- Compliance: GDPR, Kenya DPA, HIPAA, ISO 27001 (access audit trails, immutable retention policies).
- Forensics: Root-cause investigations, incident response, suspicious activity.
- Operational Analytics: Usage patterns, error hotspots, integration failures.

## 2) Events and Examples
- Authentication: LOGIN, LOGOUT, password/credential changes.
- Data access: READ, EXPORT, PRINT of PHI and financial records.
- Data mutations: CREATE, UPDATE, DELETE across entities.
- Admin: PERMISSION_CHANGE, CONFIG_CHANGE.
- Integrations: INTEGRATION outbound/inbound calls, partner activity.
- Errors: ERROR for app exceptions.

## 3) Data Model
Entity: audit_logs (JPA: com.example.codebasebackend.Entities.AuditLog)
- id (PK)
- eventType (enum): LOGIN, LOGOUT, READ, CREATE, UPDATE, DELETE, EXPORT, INTEGRATION, PERMISSION_CHANGE, CONFIG_CHANGE, ERROR
- entityType (string): e.g., "Patient"
- entityId (string): flexible ID
- user (FK) and/or username (string): actor or service name
- ipAddress (string): origin IP (X-Forwarded-For respected)
- eventTime (timestamp, UTC) — created automatically
- details (text/LOB): contextual JSON summary (args/result/duration, extra metadata)
- status (enum): SUCCESS, FAILURE, PENDING
- errorMessage (string): when failures occur
- integrationPartnerId (long): optional
- sessionId (string): web session identifier if present
- correlationId (string): request ID (X-Request-Id) for cross-system trace
- userAgent (string)
- updatedAt (timestamp)

Indexes: eventType, entityType, user_id, eventTime.

Privacy/Safety:
- Redaction: details content is sanitized to mask common secrets (password, apiKey, token, ssn, nationalId, cardNumber).
- Size cap: details truncated to 20,000 chars; errorMessage to 500 chars to prevent bloat.

## 4) REST API
Base: /api/audit-logs
- POST: create log entry manually
- GET /{id}: fetch a log
- GET (search): filters eventType, entityType, entityId, userId, username, status, integrationPartnerId, from, to (paged)
- DELETE /purge?before=ISO: purge logs older than cutoff (admin-only recommended)

Examples:
- GET /api/audit-logs?eventType=READ&entityType=Patient&entityId=123&from=2025-08-01T00:00:00Z&to=2025-08-14T00:00:00Z
- DELETE /api/audit-logs/purge?before=2025-01-01T00:00:00Z

## 5) Automatic Logging (@Auditable + AOP)
Annotation: com.example.codebasebackend.configs.Auditable
- eventType: enum value
- entityType: label (e.g., "Patient")
- entityIdExpression: SpEL such as "#id" or "#result.id"
- includeArgs: serialize method args into details (redacted/truncated)
- includeResult: serialize return object into details

Aspect: com.example.codebasebackend.configs.AuditAspect
- Wraps annotated methods, collects args/result, duration, status, and errors.
- Evaluates entityIdExpression with method params and the result object.

Already annotated endpoints:
- PatientController PATCH /api/patients/{id}/location (UPDATE)
- CHW Controller CRUD + location + nearest (CREATE/READ/UPDATE/DELETE)
- Report Controller CRUD + status/file updates (CREATE/READ/UPDATE/DELETE)
- Assistance request endpoint (CREATE)

## 6) Retention & Archiving
- Config: app.audit.retention-days (default 365)
- Job: AuditRetentionJob runs daily (03:00) and purges records older than the retention window.
- For compliance/WORM, forward logs to immutable storage (S3 Object Lock, external SIEM) or database partitioning with restricted writes.

## 7) Request Correlation
- Filter: RequestCorrelationFilter
- Accepts/sets X-Request-Id per request and stores it in correlationId; emits header in responses.
- Use in logs (MDC "requestId") and dashboards for end-to-end tracing.

## 8) Security & Access Control
- Restrict /api/audit-logs endpoints to admin/auditor roles.
- Encrypt transport (TLS) and at-rest storage as required.
- Avoid logging excessive PHI; rely on includeArgs/includeResult selectively.
- Ensure data minimization and role-based access to logs.

## 9) Extensibility and Best Practices
- Add new event types by extending AuditLog.EventType.
- Add structured metadata to "details" JSON (e.g., domain-specific keys) if needed.
- Log correlation IDs across microservices.
- Consider batching & async writers for high-volume paths.
- Add alerts on suspicious patterns (e.g., repeated failed logins, mass export).

## 10) Quick Start
- Annotate a method:
  - @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "Patient", entityIdExpression = "#id", includeArgs = true)
- Manual log:
  - POST /api/audit-logs with {"eventType":"UPDATE","entityType":"Patient","entityId":"123","status":"SUCCESS","details":"..."}
- Tune retention:
  - app.audit.retention-days=730

## 11) Testing
- Verify logs appear for annotated endpoints (e.g., patient location update) and include correlationId.
- Confirm redaction of secrets in details payloads.
- Validate purge deletes data older than the retention window.

## 12) Known limits & Next Steps
- Current redaction is regex-based; consider structured serialization + field-level redaction policies.
- Add RBAC to protect audit APIs.
- Stream logs to a SIEM for anomaly detection, dashboards, and WORM archiving.

