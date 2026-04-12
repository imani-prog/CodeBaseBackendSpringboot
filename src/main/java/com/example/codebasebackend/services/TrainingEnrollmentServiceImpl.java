// src/main/java/com/example/codebasebackend/services/impl/TrainingEnrollmentServiceImpl.java

package com.example.codebasebackend.services.impl;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.TrainingEnrollment;
import com.example.codebasebackend.Entities.TrainingEnrollment.EnrollmentStatus;
import com.example.codebasebackend.Entities.TrainingModule;
import com.example.codebasebackend.dto.EnrollmentRequest;
import com.example.codebasebackend.dto.EnrollmentResponse;
import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import com.example.codebasebackend.repositories.TrainingEnrollmentRepository;
import com.example.codebasebackend.repositories.TrainingModuleRepository;
import com.example.codebasebackend.services.TrainingEnrollmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingEnrollmentServiceImpl implements TrainingEnrollmentService {

    private final TrainingEnrollmentRepository enrollmentRepository;
    private final TrainingModuleRepository moduleRepository;
    private final CommunityHealthWorkersRepository chwRepository;

    @Override
    @Transactional
    public EnrollmentResponse enroll(Long moduleId, EnrollmentRequest request) {
        // Validate module exists and is active
        TrainingModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Training module not found: " + moduleId));

        if (!module.isActive()) {
            throw new IllegalArgumentException("Training module is not active");
        }

        // Validate CHW exists
        CommunityHealthWorkers chw = chwRepository.findById(request.getChwId())
                .orElseThrow(() -> new EntityNotFoundException("CHW not found: " + request.getChwId()));

        // Check not already enrolled
        if (enrollmentRepository.existsByChwIdAndTrainingModuleId(request.getChwId(), moduleId)) {
            throw new IllegalArgumentException("CHW is already enrolled in this module");
        }

        // Check capacity
        if (module.getMaxEnrollment() != null) {
            long currentCount = enrollmentRepository.countByTrainingModuleId(moduleId);
            if (currentCount >= module.getMaxEnrollment()) {
                throw new IllegalArgumentException("Training module is at full capacity");
            }
        }

        // Create enrollment
        TrainingEnrollment enrollment = TrainingEnrollment.builder()
                .chw(chw)
                .trainingModule(module)
                .status(EnrollmentStatus.ENROLLED)
                .notes(request.getNotes())
                .build();

        TrainingEnrollment saved = enrollmentRepository.save(enrollment);

        // Update enrolled count on module
        module.setEnrolledCount(module.getEnrolledCount() + 1);
        moduleRepository.save(module);

        return toResponse(saved);
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByModule(Long moduleId) {
        return enrollmentRepository.findEnrollmentsByModule(moduleId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByChw(Long chwId) {
        return enrollmentRepository.findByChwId(chwId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EnrollmentResponse updateProgress(Long enrollmentId, int progressPercentage) {
        TrainingEnrollment enrollment = findById(enrollmentId);
        enrollment.setProgressPercentage(progressPercentage);

        if (progressPercentage >= 100) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
            // Auto-issue certificate if module offers certification
            if (enrollment.getTrainingModule().isCertification()) {
                enrollment.setCertificateIssued(true);
            }
        } else if (progressPercentage > 0) {
            enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        }

        return toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional
    public EnrollmentResponse updateStatus(Long enrollmentId, EnrollmentStatus status) {
        TrainingEnrollment enrollment = findById(enrollmentId);
        enrollment.setStatus(status);
        if (status == EnrollmentStatus.COMPLETED) {
            enrollment.setCompletedAt(LocalDateTime.now());
            enrollment.setProgressPercentage(100);
            if (enrollment.getTrainingModule().isCertification()) {
                enrollment.setCertificateIssued(true);
            }
        }
        return toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional
    public void unenroll(Long enrollmentId) {
        TrainingEnrollment enrollment = findById(enrollmentId);
        TrainingModule module = enrollment.getTrainingModule();
        enrollmentRepository.delete(enrollment);
        // Decrement enrolled count
        if (module.getEnrolledCount() > 0) {
            module.setEnrolledCount(module.getEnrolledCount() - 1);
            moduleRepository.save(module);
        }
    }

    private TrainingEnrollment findById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found: " + id));
    }

    private EnrollmentResponse toResponse(TrainingEnrollment e) {
        CommunityHealthWorkers chw = e.getChw();
        TrainingModule module = e.getTrainingModule();
        return EnrollmentResponse.builder()
                .id(e.getId())
                .chwId(chw.getId())
                .chwName(chw.getFirstName() + " " + chw.getLastName())
                .chwCode(chw.getCode())
                .chwRegion(chw.getRegion())
                .moduleId(module.getId())
                .moduleName(module.getCourseName())
                .courseLevel(module.getCourseLevel().name())
                .status(e.getStatus())
                .progressPercentage(e.getProgressPercentage())
                .certificateIssued(e.isCertificateIssued())
                .enrolledAt(e.getEnrolledAt())
                .completedAt(e.getCompletedAt())
                .notes(e.getNotes())
                .build();
    }
}