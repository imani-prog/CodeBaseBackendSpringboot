// src/main/java/com/example/codebasebackend/repositories/TrainingEnrollmentRepository.java

package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.TrainingEnrollment;
import com.example.codebasebackend.Entities.TrainingEnrollment.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainingEnrollmentRepository extends JpaRepository<TrainingEnrollment, Long> {

    List<TrainingEnrollment> findByChwId(Long chwId);

    List<TrainingEnrollment> findByTrainingModuleId(Long moduleId);

    List<TrainingEnrollment> findByTrainingModuleIdAndStatus(Long moduleId, EnrollmentStatus status);

    Optional<TrainingEnrollment> findByChwIdAndTrainingModuleId(Long chwId, Long moduleId);

    boolean existsByChwIdAndTrainingModuleId(Long chwId, Long moduleId);

    long countByTrainingModuleId(Long moduleId);

    List<TrainingEnrollment> findByChwIdAndStatus(Long chwId, EnrollmentStatus status);

    @Query("SELECT e FROM TrainingEnrollment e WHERE e.trainingModule.id = :moduleId ORDER BY e.enrolledAt DESC")
    List<TrainingEnrollment> findEnrollmentsByModule(@Param("moduleId") Long moduleId);
}