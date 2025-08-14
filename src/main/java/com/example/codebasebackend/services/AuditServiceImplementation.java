package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.Entities.User;
import com.example.codebasebackend.dto.AuditLogRequest;
import com.example.codebasebackend.dto.AuditLogResponse;
import com.example.codebasebackend.repositories.AuditLogRepository;
import com.example.codebasebackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImplementation implements AuditService {

    private final AuditLogRepository auditRepo;
    private final UserRepository userRepo;

    private static final int MAX_DETAILS_LENGTH = 20000; // safety cap to avoid huge rows

    @Override
    public AuditLogResponse log(AuditLogRequest r) {
        if (!StringUtils.hasText(r.getEventType())) throw new ResponseStatusException(BAD_REQUEST, "eventType required");
        AuditLog e = new AuditLog();
        e.setEventType(parseEventType(r.getEventType()));
        e.setEntityType(r.getEntityType());
        e.setEntityId(r.getEntityId());
        if (r.getUserId() != null) {
            Optional<User> user = userRepo.findById(r.getUserId());
            user.ifPresent(e::setUser);
        }
        e.setUsername(StringUtils.hasText(r.getUsername()) ? r.getUsername() : currentUsername());
        e.setIpAddress(StringUtils.hasText(r.getIpAddress()) ? r.getIpAddress() : currentIp());
        e.setSessionId(StringUtils.hasText(r.getSessionId()) ? r.getSessionId() : currentSessionId());
        e.setCorrelationId(StringUtils.hasText(r.getCorrelationId()) ? r.getCorrelationId() : currentCorrelationId());
        e.setUserAgent(StringUtils.hasText(r.getUserAgent()) ? r.getUserAgent() : currentUserAgent());
        e.setStatus(parseStatus(r.getStatus()));
        e.setErrorMessage(truncate(r.getErrorMessage(), 500));
        e.setIntegrationPartnerId(r.getIntegrationPartnerId());
        String sanitized = redact(r.getDetails());
        e.setDetails(truncate(sanitized, MAX_DETAILS_LENGTH));
        AuditLog saved = auditRepo.save(e);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse get(Long id) {
        return auditRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Audit log not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> search(String eventType, String entityType, String entityId, Long userId, String username,
                                         String status, Long integrationPartnerId, OffsetDateTime from, OffsetDateTime to,
                                         Pageable pageable) {
        Specification<AuditLog> spec = andAll(
                eqEnum("eventType", eventType, true),
                like("entityType", entityType),
                like("entityId", entityId),
                eqId("user.id", userId),
                like("username", username),
                eqEnum("status", status, false),
                eqLong("integrationPartnerId", integrationPartnerId),
                between("eventTime", from, to)
        );
        return auditRepo.findAll(spec, pageable).map(this::toResponse);
    }

    // Chain non-null specifications with AND, null-safe, avoids deprecated Specification.where
    @SafeVarargs
    private final Specification<AuditLog> andAll(Specification<AuditLog>... specs) {
        Specification<AuditLog> result = null;
        if (specs == null) return null;
        for (Specification<AuditLog> s : specs) {
            if (s == null) continue;
            result = (result == null) ? s : result.and(s);
        }
        return result;
    }

    @Override
    public long purgeBefore(OffsetDateTime before) {
        if (before == null) throw new ResponseStatusException(BAD_REQUEST, "before timestamp required");
        return auditRepo.deleteByEventTimeBefore(before);
    }

    private AuditLog.EventType parseEventType(String s) {
        try { return AuditLog.EventType.valueOf(s.trim().toUpperCase()); }
        catch (Exception ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid eventType"); }
    }

    private AuditLog.EventStatus parseStatus(String s) {
        if (!StringUtils.hasText(s)) return AuditLog.EventStatus.SUCCESS;
        try { return AuditLog.EventStatus.valueOf(s.trim().toUpperCase()); }
        catch (Exception ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid status"); }
    }

    private String truncate(String s, int max) { return s == null ? null : (s.length() <= max ? s : s.substring(0, max)); }

    // naive redaction for common secrets/identifiers within JSON/Text
    private static final Pattern SECRET_KEYS = Pattern.compile("(password|apiKey|token|ssn|nationalId|cardNumber)\\s*[:=]\\s*\\\"?([A-Za-z0-9+/_\\n\\r\\-:.@]+)\\\"?", Pattern.CASE_INSENSITIVE);
    private String redact(String details) {
        if (!StringUtils.hasText(details)) return details;
        return SECRET_KEYS.matcher(details).replaceAll("$1:\"***REDACTED***\"");
    }

    private String currentIp() {
        HttpServletRequest req = currentRequest();
        if (req == null) return null;
        String ip = req.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip)) return ip.split(",")[0].trim();
        return req.getRemoteAddr();
    }
    private String currentUserAgent() {
        HttpServletRequest req = currentRequest();
        return req != null ? req.getHeader("User-Agent") : null;
    }
    private String currentSessionId() {
        HttpServletRequest req = currentRequest();
        return req != null && req.getSession(false) != null ? req.getSession(false).getId() : null;
    }
    private String currentCorrelationId() {
        HttpServletRequest req = currentRequest();
        return req != null ? req.getHeader("X-Request-Id") : null;
    }
    private String currentUsername() { return "system"; }

    private HttpServletRequest currentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) return sra.getRequest();
        return null;
    }

    // JPA Specifications helpers
    private Specification<AuditLog> like(String field, String value) {
        return (root, q, cb) -> !StringUtils.hasText(value) ? null : cb.like(cb.lower(getPath(root, field).as(String.class)), "%" + value.toLowerCase() + "%");
    }

    private Specification<AuditLog> eqEnum(String field, String value, boolean isEventType) {
        if (!StringUtils.hasText(value)) return null;
        return (root, q, cb) -> {
            if (isEventType) return cb.equal(getPath(root, field), AuditLog.EventType.valueOf(value.trim().toUpperCase()));
            else return cb.equal(getPath(root, field), AuditLog.EventStatus.valueOf(value.trim().toUpperCase()));
        };
    }

    private Specification<AuditLog> eqLong(String field, Long value) {
        return (root, q, cb) -> value == null ? null : cb.equal(getPath(root, field), value);
    }

    private Specification<AuditLog> eqId(String field, Long value) { // for nested id (user.id)
        return (root, q, cb) -> value == null ? null : cb.equal(getPath(root, field), value);
    }

    private Specification<AuditLog> between(String field, OffsetDateTime from, OffsetDateTime to) {
        return (root, q, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(getPath(root, field), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(getPath(root, field), from);
            return cb.lessThanOrEqualTo(getPath(root, field), to);
        };
    }

    private <T> jakarta.persistence.criteria.Path<T> getPath(jakarta.persistence.criteria.Root<AuditLog> root, String field) {
        if (!field.contains(".")) return root.get(field);
        String[] parts = field.split("\\.");
        jakarta.persistence.criteria.Path<?> p = root.get(parts[0]);
        for (int i = 1; i < parts.length; i++) p = p.get(parts[i]);
        @SuppressWarnings("unchecked")
        jakarta.persistence.criteria.Path<T> casted = (jakarta.persistence.criteria.Path<T>) p;
        return casted;
    }

    private AuditLogResponse toResponse(AuditLog e) {
        AuditLogResponse dto = new AuditLogResponse();
        dto.setId(e.getId());
        dto.setEventType(e.getEventType() != null ? e.getEventType().name() : null);
        dto.setEntityType(e.getEntityType());
        dto.setEntityId(e.getEntityId());
        dto.setUserId(e.getUser() != null ? e.getUser().getId() : null);
        dto.setUsername(e.getUsername());
        dto.setIpAddress(e.getIpAddress());
        dto.setEventTime(e.getEventTime());
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setErrorMessage(e.getErrorMessage());
        dto.setIntegrationPartnerId(e.getIntegrationPartnerId());
        dto.setSessionId(e.getSessionId());
        dto.setCorrelationId(e.getCorrelationId());
        dto.setUserAgent(e.getUserAgent());
        dto.setDetails(e.getDetails());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }
}
