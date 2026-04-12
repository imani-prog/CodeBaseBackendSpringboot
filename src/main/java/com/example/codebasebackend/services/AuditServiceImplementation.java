package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.Entities.User;
import com.example.codebasebackend.dto.AuditLogRequest;
import com.example.codebasebackend.dto.AuditLogResponse;
import com.example.codebasebackend.repositories.AuditLogRepository;
import com.example.codebasebackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Locale;
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

        String authenticatedUsername = currentUsername();
        String requestedUsername = StringUtils.hasText(r.getUsername()) ? r.getUsername().trim() : null;

        User resolvedUser = null;
        if (r.getUserId() != null) {
            resolvedUser = userRepo.findById(r.getUserId()).orElse(null);
        }
        if (resolvedUser == null && StringUtils.hasText(requestedUsername)) {
            resolvedUser = userRepo.findByUsernameIgnoreCase(requestedUsername)
                    .or(() -> userRepo.findByEmailIgnoreCase(requestedUsername))
                    .orElse(null);
        }
        if (resolvedUser == null && StringUtils.hasText(authenticatedUsername) && !"system".equalsIgnoreCase(authenticatedUsername)) {
            resolvedUser = userRepo.findByUsernameIgnoreCase(authenticatedUsername)
                    .or(() -> userRepo.findByEmailIgnoreCase(authenticatedUsername))
                    .orElse(null);
        }

        if (resolvedUser != null) {
            e.setUser(resolvedUser);
        }

        String finalUsername = requestedUsername;
        if (!StringUtils.hasText(finalUsername) && resolvedUser != null) {
            finalUsername = resolvedUser.getUsername();
        }
        if (!StringUtils.hasText(finalUsername)) {
            finalUsername = authenticatedUsername;
        }
        e.setUsername(finalUsername);

        String entityId = StringUtils.hasText(r.getEntityId()) ? r.getEntityId().trim() : null;
        if (!StringUtils.hasText(entityId)
                && "USER".equalsIgnoreCase(r.getEntityType())
                && resolvedUser != null
                && resolvedUser.getId() != null) {
            entityId = String.valueOf(resolvedUser.getId());
        }
        if (!StringUtils.hasText(entityId)
                && (e.getEventType() == AuditLog.EventType.LOGIN || e.getEventType() == AuditLog.EventType.LOGOUT)
                && StringUtils.hasText(finalUsername)) {
            entityId = finalUsername;
        }
        e.setEntityId(entityId);

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
                                         String searchTerm, String status, Long integrationPartnerId, OffsetDateTime from, OffsetDateTime to,
                                         Pageable pageable) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(BAD_REQUEST, "'from' must be before or equal to 'to'");
        }

        AuditLog.EventType parsedEventType = parseOptionalEventType(eventType);
        AuditLog.EventStatus parsedStatus = parseOptionalStatus(status);
        String resolvedUsernameFromUserId = null;
        if (userId != null) {
            resolvedUsernameFromUserId = userRepo.findById(userId).map(User::getUsername).orElse(null);
        }

        Pageable safePageable = pageable;
        if (pageable == null || pageable.getSort().isUnsorted()) {
            safePageable = PageRequest.of(
                    pageable != null ? pageable.getPageNumber() : 0,
                    pageable != null ? pageable.getPageSize() : 20,
                    Sort.by(Sort.Direction.DESC, "eventTime")
            );
        }

        Specification<AuditLog> spec = andAll(
                eqEventType(parsedEventType),
                like("entityType", entityType),
                like("entityId", entityId),
                userIdentityFilter(userId, username, resolvedUsernameFromUserId),
                containsAny(searchTerm),
                eqStatus(parsedStatus),
                eqLong("integrationPartnerId", integrationPartnerId),
                between("eventTime", from, to)
        );
        return auditRepo.findAll(spec, safePageable).map(this::toResponse);
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

    private AuditLog.EventType parseOptionalEventType(String s) {
        if (!StringUtils.hasText(s) || "all".equalsIgnoreCase(s.trim())) return null;
        try {
            return AuditLog.EventType.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid eventType");
        }
    }

    private AuditLog.EventStatus parseStatus(String s) {
        if (!StringUtils.hasText(s)) return AuditLog.EventStatus.SUCCESS;
        try { return AuditLog.EventStatus.valueOf(s.trim().toUpperCase()); }
        catch (Exception ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid status"); }
    }

    private AuditLog.EventStatus parseOptionalStatus(String s) {
        if (!StringUtils.hasText(s) || "all".equalsIgnoreCase(s.trim())) return null;
        try {
            return AuditLog.EventStatus.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid status");
        }
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
    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && StringUtils.hasText(auth.getName())
                && !"anonymousUser".equalsIgnoreCase(auth.getName())) {
            return auth.getName();
        }
        return "system";
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) return sra.getRequest();
        return null;
    }

    // JPA Specifications helpers
    private Specification<AuditLog> like(String field, String value) {
        return (root, q, cb) -> !StringUtils.hasText(value) ? null : cb.like(cb.lower(getPath(root, field).as(String.class)), "%" + value.toLowerCase() + "%");
    }

    private Specification<AuditLog> eqEventType(AuditLog.EventType value) {
        return (root, q, cb) -> value == null ? null : cb.equal(root.get("eventType"), value);
    }

    private Specification<AuditLog> eqStatus(AuditLog.EventStatus value) {
        return (root, q, cb) -> value == null ? null : cb.equal(root.get("status"), value);
    }

    private Specification<AuditLog> eqLong(String field, Long value) {
        return (root, q, cb) -> value == null ? null : cb.equal(getPath(root, field), value);
    }

    private Specification<AuditLog> eqId(String field, Long value) { // for nested id (user.id)
        return (root, q, cb) -> value == null ? null : cb.equal(getPath(root, field), value);
    }

    private Specification<AuditLog> userIdentityFilter(Long userId, String username, String resolvedUsernameFromUserId) {
        return (root, q, cb) -> {
            boolean hasUserId = userId != null;
            boolean hasUsername = StringUtils.hasText(username);
            if (!hasUserId && !hasUsername) {
                return null;
            }

            jakarta.persistence.criteria.Predicate userIdPredicate = null;
            if (hasUserId) {
                jakarta.persistence.criteria.Predicate byLinkedUser = cb.equal(getPath(root, "user.id"), userId);
                if (StringUtils.hasText(resolvedUsernameFromUserId)) {
                    jakarta.persistence.criteria.Predicate byUsernameFallback = cb.equal(
                            cb.lower(root.get("username")),
                            resolvedUsernameFromUserId.toLowerCase(Locale.ROOT)
                    );
                    userIdPredicate = cb.or(byLinkedUser, byUsernameFallback);
                } else {
                    userIdPredicate = byLinkedUser;
                }
            }

            jakarta.persistence.criteria.Predicate usernamePredicate = null;
            if (hasUsername) {
                String like = "%" + username.toLowerCase(Locale.ROOT) + "%";
                usernamePredicate = cb.like(cb.lower(root.get("username")), like);
            }

            if (userIdPredicate != null && usernamePredicate != null) {
                return cb.and(userIdPredicate, usernamePredicate);
            }
            return userIdPredicate != null ? userIdPredicate : usernamePredicate;
        };
    }

    private Specification<AuditLog> between(String field, OffsetDateTime from, OffsetDateTime to) {
        return (root, q, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(getPath(root, field), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(getPath(root, field), from);
            return cb.lessThanOrEqualTo(getPath(root, field), to);
        };
    }

    private Specification<AuditLog> containsAny(String searchTerm) {
        return (root, q, cb) -> {
            if (!StringUtils.hasText(searchTerm)) return null;
            String like = "%" + searchTerm.toLowerCase(Locale.ROOT) + "%";
            String rawLike = "%" + searchTerm + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("username")), like),
                    cb.like(cb.lower(root.get("entityType")), like),
                    cb.like(cb.lower(root.get("entityId")), like),
                    cb.like(cb.lower(root.get("ipAddress")), like),
                    cb.like(cb.lower(root.get("sessionId")), like),
                    cb.like(cb.lower(root.get("correlationId")), like),
                    // details is @Lob(CLOB); avoid lower(details) which breaks on some DBs.
                    cb.like(root.get("details"), rawLike),
                    cb.like(cb.lower(root.get("errorMessage")), like)
            );
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
        User responseUser = resolveUserForResponse(e);

        dto.setId(e.getId());
        dto.setEventType(e.getEventType() != null ? e.getEventType().name() : null);
        dto.setEntityType(e.getEntityType());
        dto.setEntityId(StringUtils.hasText(e.getEntityId()) ? e.getEntityId() : "-");
        dto.setUserId(responseUser != null ? responseUser.getId() : null);

        String username = StringUtils.hasText(e.getUsername()) ? e.getUsername() : (responseUser != null ? responseUser.getUsername() : "system");
        String fullName = responseUser != null && StringUtils.hasText(responseUser.getFullName())
                ? responseUser.getFullName()
                : null;
        String displayName = StringUtils.hasText(fullName) ? fullName : username;

        dto.setUsername(username);
        dto.setFullName(fullName);
        dto.setUserDisplayName(displayName);
        dto.setUserRole(responseUser != null && responseUser.getRole() != null ? responseUser.getRole().name() : "SYSTEM");
        dto.setIpAddress(e.getIpAddress());
        dto.setEventTime(e.getEventTime());
        dto.setPerformedAt(e.getEventTime());
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setErrorMessage(e.getErrorMessage());
        dto.setFailureReason(e.getErrorMessage());
        dto.setIntegrationPartnerId(e.getIntegrationPartnerId());
        dto.setSessionId(e.getSessionId());
        dto.setCorrelationId(e.getCorrelationId());
        dto.setUserAgent(e.getUserAgent());
        dto.setDetails(e.getDetails());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    private User resolveUserForResponse(AuditLog e) {
        if (e.getUser() != null) {
            return e.getUser();
        }
        if (!StringUtils.hasText(e.getUsername())) {
            return null;
        }
        return userRepo.findByUsernameIgnoreCase(e.getUsername())
                .or(() -> userRepo.findByEmailIgnoreCase(e.getUsername()))
                .orElse(null);
    }
}
