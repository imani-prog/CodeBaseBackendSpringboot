package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportFileUpdateRequest {
    @NotBlank
    private String fileUrl;
}

