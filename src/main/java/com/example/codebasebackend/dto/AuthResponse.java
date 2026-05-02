package com.example.codebasebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor          // ← Lombok generates the ONE constructor with all 5 fields
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String role;
    private Long   userId;

}