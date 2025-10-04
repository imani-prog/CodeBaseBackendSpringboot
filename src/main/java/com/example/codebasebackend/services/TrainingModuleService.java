package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.TrainingModule;
import com.example.codebasebackend.Entities.TrainingModule.CourseLevel;

import java.util.List;

public interface TrainingModuleService {

    // Basic CRUD operations

    TrainingModule createTrainingModule(TrainingModule trainingModule);

    TrainingModule getTrainingModuleById(Long id);

    List<TrainingModule> getAllTrainingModules();

    List<TrainingModule> getAllActiveTrainingModules();

    TrainingModule updateTrainingModule(Long id, TrainingModule updatedModule);

    void deleteTrainingModule(Long id);

    void deactivateTrainingModule(Long id);

    // Query methods
    List<TrainingModule> getTrainingModulesByLevel(CourseLevel level);

    List<TrainingModule> getTrainingModulesByInstructor(String instructorName);

    List<TrainingModule> getCertificationCourses();

    List<TrainingModule> getTopRatedCourses();

    List<TrainingModule> getCoursesByMinRating(double minRating);

    List<TrainingModule> getCoursesWithAvailableSlots();

    List<TrainingModule> searchCourses(String keyword);

    List<TrainingModule> getCoursesByTag(String tag);

    // Business operations
    TrainingModule enrollStudent(Long moduleId);

    TrainingModule addRating(Long moduleId, double rating, String comment);
}