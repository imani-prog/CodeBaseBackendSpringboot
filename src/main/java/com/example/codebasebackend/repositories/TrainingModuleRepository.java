package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.TrainingModule;
import com.example.codebasebackend.Entities.TrainingModule.CourseLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingModuleRepository extends JpaRepository<TrainingModule, Long> {

    // Basic queries - Spring Data JPA auto-generates these
    List<TrainingModule> findByIsActiveTrue();

    List<TrainingModule> findByCourseLevelAndIsActiveTrue(CourseLevel level);

    List<TrainingModule> findByInstructorNameContainingIgnoreCaseAndIsActiveTrue(String instructorName);

    List<TrainingModule> findByCertificationTrueAndIsActiveTrue();

    List<TrainingModule> findByEnrollNowAvailableTrueAndIsActiveTrue();

    List<TrainingModule> findByRatingGreaterThanEqualAndIsActiveTrueOrderByRatingDesc(double rating);

    List<TrainingModule> findByIsActiveTrueOrderByRatingDescEnrolledCountDesc();

    // Custom queries with @Query annotation
    @Query("SELECT tm FROM TrainingModule tm WHERE " +
            "(LOWER(tm.courseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(tm.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(tm.instructorName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND tm.isActive = true")
    List<TrainingModule> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT tm FROM TrainingModule tm WHERE " +
            "(tm.maxEnrollment IS NULL OR tm.enrolledCount < tm.maxEnrollment) " +
            "AND tm.isActive = true AND tm.enrollNowAvailable = true")
    List<TrainingModule> findCoursesWithAvailableSlots();

    @Query("SELECT DISTINCT tm FROM TrainingModule tm JOIN tm.tags t WHERE " +
            "LOWER(t) LIKE LOWER(CONCAT('%', :tag, '%')) AND tm.isActive = true")
    List<TrainingModule> findByTagsContainingIgnoreCase(@Param("tag") String tag);
}