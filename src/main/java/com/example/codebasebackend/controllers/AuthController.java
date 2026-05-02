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
        // AuthService handles token generation — do NOT add code after this return
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        // Delegate entirely to AuthService — jwtUtil belongs there, not in a controller
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(400).body(Map.of("message", "Refresh token is required"));
        }
        return ResponseEntity.ok(authService.refresh(refreshToken));
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