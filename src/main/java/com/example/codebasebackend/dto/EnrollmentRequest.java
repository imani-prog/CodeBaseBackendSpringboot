// src/main/java/com/example/codebasebackend/dto/EnrollmentRequest.java

package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequest {

    @NotNull(message = "CHW ID is required")
    private Long chwId;

    private String notes;
}