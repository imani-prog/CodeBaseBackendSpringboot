package com.example.codebasebackend.controllers;

import com.example.codebasebackend.configs.JwtUtil;
import com.example.codebasebackend.dto.RegisterRequest;
import com.example.codebasebackend.services.AuthService;
import com.example.codebasebackend.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private DataSource dataSource;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @Test
    void registerReturnsConflictWhenUsernameAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("timothy");
        request.setEmail("timothyimani6@gmail.com");
        request.setPassword("Timo123");
        request.setFullName("Timothy Imani");
        request.setPhone("0743669252");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }
}



