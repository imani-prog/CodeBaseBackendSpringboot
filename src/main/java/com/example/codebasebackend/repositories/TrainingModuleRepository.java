package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.TrainingModule;
import com.example.codebasebackend.Entities.TrainingModule.CourseLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingModuleRepository extends JpaRepository<TrainingModule, Long> {

    // Find active courses
    List<TrainingModule> findByIsActiveTrue();

    // Find by course level
    List<TrainingModule> findByCourseLevelAndIsActiveTrue(CourseLevel courseLevel);

    // Find by instructor
    List<TrainingModule> findByInstructorNameContainingIgnoreCaseAndIsActiveTrue(String instructorName);

    // Find courses with enrollment available
    List<TrainingModule> findByEnrollNowAvailableTrueAndIsActiveTrue();

    // Find courses with certification
    List<TrainingModule> findByCertificationTrueAndIsActiveTrue();

    // Find courses by rating range
    @Query("SELECT tm FROM TrainingModule tm WHERE tm.rating >= :minRating AND tm.isActive = true ORDER BY tm.rating DESC")
    List<TrainingModule> findByRatingGreaterThanEqualAndIsActiveTrue(@Param("minRating") double minRating);

    // Search courses by name or description
    @Query("SELECT tm FROM TrainingModule tm WHERE (LOWER(tm.courseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(tm.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND tm.isActive = true")
    List<TrainingModule> searchByKeyword(@Param("keyword") String keyword);

    // Find courses with available enrollment slots
    @Query("SELECT tm FROM TrainingModule tm WHERE tm.maxEnrollment IS NULL OR tm.enrolledCount < tm.maxEnrollment AND tm.isActive = true AND tm.enrollNowAvailable = true")
    List<TrainingModule> findCoursesWithAvailableSlots();

    // Find top rated courses
    @Query("SELECT tm FROM TrainingModule tm WHERE tm.isActive = true ORDER BY tm.rating DESC, tm.enrolledCount DESC")
    List<TrainingModule> findTopRatedCourses();

    // Find courses by tags
    @Query("SELECT DISTINCT tm FROM TrainingModule tm JOIN tm.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :tag, '%')) AND tm.isActive = true")
    List<TrainingModule> findByTagsContainingIgnoreCase(@Param("tag") String tag);
}
