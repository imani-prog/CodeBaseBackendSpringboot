// src/main/java/com/example/codebasebackend/services/TrainingEnrollmentService.java

package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.EnrollmentRequest;
import com.example.codebasebackend.dto.EnrollmentResponse;
import com.example.codebasebackend.Entities.TrainingEnrollment.EnrollmentStatus;

import java.util.List;

public interface TrainingEnrollmentService {
    EnrollmentResponse enroll(Long moduleId, EnrollmentRequest request);
    List<EnrollmentResponse> getEnrollmentsByModule(Long moduleId);
    List<EnrollmentResponse> getEnrollmentsByChw(Long chwId);
    EnrollmentResponse updateProgress(Long enrollmentId, int progressPercentage);
    EnrollmentResponse updateStatus(Long enrollmentId, EnrollmentStatus status);
    void unenroll(Long enrollmentId);
}