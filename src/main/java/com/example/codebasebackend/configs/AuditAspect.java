package com.example.codebasebackend.configs;

import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.dto.AuditLogRequest;
import com.example.codebasebackend.services.AuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(com.example.codebasebackend.configs.Auditable)")
    public Object aroundAuditable(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();
        Auditable ann = method.getAnnotation(Auditable.class);
        String entityType = ann.entityType();
        String preEntityId = evaluateEntityId(ann.entityIdExpression(), sig, pjp.getArgs(), null);

        String preDetails = null;
        if (ann.includeArgs()) {
            preDetails = toJsonSafe(Map.of("args", argsMap(sig, pjp.getArgs())));
        }

        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            String postDetails = preDetails;
            if (ann.includeResult()) {
                Map<String, Object> map = new HashMap<>();
                if (preDetails != null) {
                    map.put("pre", preDetails);
                }
                map.put("result", result);
                map.put("durationMs", System.currentTimeMillis() - start);
                postDetails = toJsonSafe(map);
            }

            String finalEntityId = preEntityId;
            String maybePost = evaluateEntityId(ann.entityIdExpression(), sig, pjp.getArgs(), result);
            if (maybePost != null) finalEntityId = maybePost;

            AuditLogRequest req = new AuditLogRequest();
            req.setEventType(ann.eventType().name());
            req.setEntityType(entityType);
            req.setEntityId(finalEntityId);
            req.setStatus(AuditLog.EventStatus.SUCCESS.name());
            req.setDetails(postDetails);
            auditService.log(req);
            return result;
        } catch (Throwable ex) {
            AuditLogRequest req = new AuditLogRequest();
            req.setEventType(ann.eventType().name());
            req.setEntityType(entityType);
            req.setEntityId(preEntityId);
            req.setStatus(AuditLog.EventStatus.FAILURE.name());
            req.setErrorMessage(ex.getMessage());
            Map<String, Object> map = new HashMap<>();
            if (preDetails != null) map.put("pre", preDetails);
            map.put("exception", ex.getClass().getName());
            map.put("durationMs", System.currentTimeMillis() - start);
            req.setDetails(toJsonSafe(map));
            try { auditService.log(req); } catch (Exception logEx) { log.warn("Failed to write audit log: {}", logEx.getMessage()); }
            throw ex;
        }
    }

    private String evaluateEntityId(String spel, MethodSignature sig, Object[] args, Object result) {
        if (spel == null || spel.isBlank()) return null;
        try {
            StandardEvaluationContext ctx = new StandardEvaluationContext();
            String[] paramNames = sig.getParameterNames();
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length && i < args.length; i++) ctx.setVariable(paramNames[i], args[i]);
            }
            ctx.setVariable("result", result);
            Expression exp = parser.parseExpression(spel.startsWith("#") ? spel : ("#" + spel));
            Object val = exp.getValue(ctx);
            return val != null ? String.valueOf(val) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> argsMap(MethodSignature sig, Object[] args) {
        Map<String, Object> map = new HashMap<>();
        String[] names = sig.getParameterNames();
        if (names != null) {
            for (int i = 0; i < names.length && i < args.length; i++) map.put(names[i], args[i]);
        }
        return map;
    }

    private String toJsonSafe(Object o) {
        try { return objectMapper.writeValueAsString(o); }
        catch (JsonProcessingException e) { return String.valueOf(o); }
    }
}
