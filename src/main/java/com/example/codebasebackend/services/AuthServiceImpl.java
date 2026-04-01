package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.User;
import com.example.codebasebackend.Entities.UserRole;
import com.example.codebasebackend.Entities.UserStatus;
import com.example.codebasebackend.configs.JwtUtil;
import com.example.codebasebackend.dto.AuthResponse;
import com.example.codebasebackend.dto.LoginRequest;
import com.example.codebasebackend.dto.RegisterRequest;
import com.example.codebasebackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : UserRole.PATIENT)
                .status(UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getUsername(), saved.getRole().name());
        return new AuthResponse(token, saved.getUsername(), saved.getRole().name(), saved.getId());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String loginIdentifier = request.getUsername();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginIdentifier, request.getPassword())
        );

        User user = userRepository.findByUsernameIgnoreCase(loginIdentifier)
                .or(() -> userRepository.findByEmailIgnoreCase(loginIdentifier))
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole().name(), user.getId());
    }
}