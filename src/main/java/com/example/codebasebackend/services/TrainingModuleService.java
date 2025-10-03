package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.TrainingModule;
import com.example.codebasebackend.Entities.TrainingModule.CourseLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TrainingModuleService {

    TrainingModule createTrainingModule(TrainingModule trainingModule);

    Optional<TrainingModule> getTrainingModuleById(Long id);

    List<TrainingModule> getAllActiveTrainingModules();

    Page<TrainingModule> getAllTrainingModules(Pageable pageable);

    TrainingModule updateTrainingModule(Long id, TrainingModule updatedModule);

    void deleteTrainingModule(Long id);

    void deactivateTrainingModule(Long id);

    List<TrainingModule> getTrainingModulesByLevel(CourseLevel level);

    List<TrainingModule> getTrainingModulesByInstructor(String instructorName);

    List<TrainingModule> getAvailableForEnrollment();

    List<TrainingModule> getCertificationCourses();

    List<TrainingModule> getTopRatedCourses();

    List<TrainingModule> searchCourses(String keyword);

    List<TrainingModule> getCoursesByTag(String tag);

    List<TrainingModule> getCoursesByMinRating(double minRating);

    TrainingModule enrollStudent(Long moduleId);

    TrainingModule updateRating(Long moduleId, double newRating, String comment);
}
