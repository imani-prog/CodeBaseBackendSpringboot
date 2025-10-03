package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.TrainingModule;
import com.example.codebasebackend.Entities.TrainingModule.CourseLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingModuleRepository extends JpaRepository<TrainingModule, Long> {

    // Active courses
    List<TrainingModule> findByIsActiveTrue();

    // By course level (only active)
    List<TrainingModule> findByCourseLevelAndIsActiveTrue(CourseLevel level);

    // By instructor (only active)
    List<TrainingModule> findByInstructorNameContainingIgnoreCaseAndIsActiveTrue(String instructorName);

    // Certification courses (only active)
    List<TrainingModule> findByCertificationTrueAndIsActiveTrue();

    // By course name (for search functionality)
    List<TrainingModule> findByCourseNameContainingIgnoreCaseAndIsActiveTrue(String courseName);

    // Courses with minimum rating (ordered by rating)
    List<TrainingModule> findByRatingGreaterThanEqualAndIsActiveTrueOrderByRatingDesc(double rating);

    // Recent courses (ordered by creation date)
    List<TrainingModule> findByIsActiveTrueOrderByCreatedAtDesc();

    // Top rated courses (ordered by rating and enrollment)
    List<TrainingModule> findByIsActiveTrueOrderByRatingDescEnrolledCountDesc();

    // Courses with enrollment available
    List<TrainingModule> findByEnrollNowAvailableTrueAndIsActiveTrue();
}
