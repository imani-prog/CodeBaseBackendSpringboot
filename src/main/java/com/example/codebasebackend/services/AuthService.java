package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.AuthResponse;
import com.example.codebasebackend.dto.LoginRequest;
import com.example.codebasebackend.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}