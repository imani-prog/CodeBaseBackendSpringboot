package com.example.codebasebackend.configs;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadCredentialsReturnsUnauthorized() {
        ResponseEntity<Map<String, Object>> response = handler.handleBadCredentials(new BadCredentialsException("Bad credentials"));
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(body);
        assertEquals(401, body.get("status"));
        assertEquals("Unauthorized", body.get("error"));
        assertEquals("Invalid username/email or password", body.get("message"));
    }
}


