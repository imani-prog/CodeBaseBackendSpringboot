package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.TrainingModule;
import com.example.codebasebackend.Entities.TrainingModule.CourseLevel;
import com.example.codebasebackend.repositories.TrainingModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainingModuleServiceImplementation implements TrainingModuleService {

    private final TrainingModuleRepository trainingModuleRepository;

    @Override
    public TrainingModule createTrainingModule(TrainingModule trainingModule) {
        if (trainingModule.getCourseModules() == null || trainingModule.getCourseModules().size() < 5) {
            throw new IllegalArgumentException("Course must have at least 5 modules");
        }
        return trainingModuleRepository.save(trainingModule);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingModule getTrainingModuleById(Long id) {
        return trainingModuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training module not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingModule> getAllTrainingModules() {
        return trainingModuleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingModule> getAllActiveTrainingModules() {
        return trainingModuleRepository.findByIsActiveTrue();
    }

    @Override
    public TrainingModule updateTrainingModule(Long id, TrainingModule updatedModule) {
        return trainingModuleRepository.findById(id)
                .map(existingModule -> {
                    existingModule.setCourseName(updatedModule.getCourseName());
                    existingModule.setCourseLevel(updatedModule.getCourseLevel());
                    existingModule.setDuration(updatedModule.getDuration());
                    existingModule.setRating(updatedModule.getRating()); // Added missing rating field
                    existingModule.setDescription(updatedModule.getDescription());
                    existingModule.setBrochureUrl(updatedModule.getBrochureUrl());
                    existingModule.setCertification(updatedModule.isCertification());
                    existingModule.setCourseModules(updatedModule.getCourseModules());
                    existingModule.setEnrollNowAvailable(updatedModule.isEnrollNowAvailable());
                    existingModule.setPrerequisites(updatedModule.getPrerequisites());
                    existingModule.setInstructorName(updatedModule.getInstructorName());
                    existingModule.setTags(updatedModule.getTags());
                    existingModule.setPrice(updatedModule.getPrice());
                    existingModule.setMaxEnrollment(updatedModule.getMaxEnrollment());
                    existingModule.setActive(updatedModule.isActive()); // Also added isActive field
                    return trainingModuleRepository.save(existingModule);
                })
                .orElseThrow(() -> new RuntimeException("Training module not found with id: " + id));
    }

    @Override
    public void deleteTrainingModule(Long id) {
        if (!trainingModuleRepository.existsById(id)) {
            throw new RuntimeException("Training module not found with id: " + id);
        }
        trainingModuleRepository.deleteById(id);
    }

    @Override
    public void deactivateTrainingModule(Long id) {
        trainingModuleRepository.findById(id)
                .ifPresentOrElse(
                        module -> {
                            module.setActive(false);
                            trainingModuleRepository.save(module);
                        },
                        () -> { throw new RuntimeException("Training module not found with id: " + id); }
                );
    }

    @Override
    public void activateTrainingModule(Long id) {
        trainingModuleRepository.findById(id)
                .ifPresentOrElse(
                        module -> {
                            module.setActive(true);
                            trainingModuleRepository.save(module);
                        },
                        () -> { throw new RuntimeException("Training module not found with id: " + id); }
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingModule> getTrainingModulesByLevel(CourseLevel level) {
        return trainingModuleRepository.findByCourseLevelAndIsActiveTrue(level);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingModule> getTrainingModulesByInstructor(String instructorName) {
        return trainingModuleRepository.findByInstructorNameContainingIgnoreCaseAndIsActiveTrue(instructorName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingModule> getCertificationCourses() {
        return trainingModuleRepository.findByCertificationTrueAndIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingModule> getTopRatedCourses() {
        return trainingModuleRepository.findByIsActiveTrueOrderByRatingDescEnrolledCountDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingModule> getCoursesByMinRating(double minRating) {
        return trainingModuleRepository.findByRatingGreaterThanEqualAndIsActiveTrueOrderByRatingDesc(minRating);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingModule> getCoursesWithAvailableSlots() {
        return trainingModuleRepository.findCoursesWithAvailableSlots();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingModule> searchCourses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveTrainingModules();
        }
        return trainingModuleRepository.searchByKeyword(keyword.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingModule> getCoursesByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return List.of();
        }
        return trainingModuleRepository.findByTagsContainingIgnoreCase(tag.trim());
    }

    @Override
    public TrainingModule enrollStudent(Long moduleId) {
        return trainingModuleRepository.findById(moduleId)
                .map(module -> {
                    if (!module.isEnrollNowAvailable()) {
                        throw new IllegalStateException("Enrollment is not available for this course");
                    }
                    if (module.getMaxEnrollment() != null &&
                            module.getEnrolledCount() >= module.getMaxEnrollment()) {
                        throw new IllegalStateException("Course is full");
                    }
                    module.setEnrolledCount(module.getEnrolledCount() + 1);
                    return trainingModuleRepository.save(module);
                })
                .orElseThrow(() -> new RuntimeException("Training module not found with id: " + moduleId));
    }

    @Override
    public TrainingModule addRating(Long moduleId, double newRating, String comment) {
        if (newRating < 0.0 || newRating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        }

        return trainingModuleRepository.findById(moduleId)
                .map(module -> {
                    double currentRating = module.getRating();
                    int enrolledCount = module.getEnrolledCount();

                    // Calculate new average rating
                    double newAverageRating;
                    if (enrolledCount > 0) {
                        newAverageRating = ((currentRating * enrolledCount) + newRating) / (enrolledCount + 1);
                    } else {
                        newAverageRating = newRating;
                    }

                    module.setRating(Math.round(newAverageRating * 10.0) / 10.0);

                    if (comment != null && !comment.trim().isEmpty()) {
                        module.getComments().add(comment.trim());
                    }

                    return trainingModuleRepository.save(module);
                })
                .orElseThrow(() -> new RuntimeException("Training module not found with id: " + moduleId));
    }
}