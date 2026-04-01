package com.example.codebasebackend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    @JsonAlias({"email", "identifier"})
    private String username;

    @NotBlank
    private String password;
}