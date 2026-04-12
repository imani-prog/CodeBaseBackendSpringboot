package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.dto.AuditLogRequest;
import com.example.codebasebackend.dto.AuthResponse;
import com.example.codebasebackend.dto.LoginRequest;
import com.example.codebasebackend.dto.RegisterRequest;
import com.example.codebasebackend.services.AuditService;
import com.example.codebasebackend.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuditService auditService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        AuditLogRequest req = new AuditLogRequest();
        req.setEventType(AuditLog.EventType.LOGOUT.name());
        req.setEntityType("User");
        req.setEntityId(username);
        req.setUsername(username);
        req.setStatus(AuditLog.EventStatus.SUCCESS.name());
        req.setDetails("{\"action\":\"logout\",\"username\":\"" + username + "\"}");
        auditService.log(req);
        return ResponseEntity.ok(Map.of("message", "Logout recorded"));
    }
}