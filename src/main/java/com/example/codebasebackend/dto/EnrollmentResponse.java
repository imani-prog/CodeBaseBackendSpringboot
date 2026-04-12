// src/main/java/com/example/codebasebackend/dto/EnrollmentResponse.java

package com.example.codebasebackend.dto;

import com.example.codebasebackend.Entities.TrainingEnrollment.EnrollmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EnrollmentResponse {

    private Long id;
    private Long chwId;
    private String chwName;
    private String chwCode;
    private String chwRegion;
    private Long moduleId;
    private String moduleName;
    private String courseLevel;
    private EnrollmentStatus status;
    private int progressPercentage;
    private boolean certificateIssued;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private String notes;
}