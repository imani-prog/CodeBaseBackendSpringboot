package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.TrainingModule;
import com.example.codebasebackend.Entities.TrainingModule.CourseLevel;
import com.example.codebasebackend.repositories.TrainingModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainingModuleServiceImplementation implements TrainingModuleService {

    @Autowired
    private TrainingModuleRepository trainingModuleRepository;

    @Override
    public TrainingModule createTrainingModule(TrainingModule trainingModule) {
        // Validate that course has at least 5 modules
        if (trainingModule.getCourseModules() == null || trainingModule.getCourseModules().size() < 5) {
            throw new IllegalArgumentException("Course must have at least 5 modules");
        }
        return trainingModuleRepository.save(trainingModule);
    }

    @Override
    public Optional<TrainingModule> getTrainingModuleById(Long id) {
        return trainingModuleRepository.findById(id);
    }

    @Override
    public List<TrainingModule> getAllActiveTrainingModules() {
        return trainingModuleRepository.findByIsActiveTrue();
    }

    @Override
    public Page<TrainingModule> getAllTrainingModules(Pageable pageable) {
        return trainingModuleRepository.findAll(pageable);
    }

    @Override
    public TrainingModule updateTrainingModule(Long id, TrainingModule updatedModule) {
        return trainingModuleRepository.findById(id)
                .map(existingModule -> {
                    existingModule.setCourseName(updatedModule.getCourseName());
                    existingModule.setCourseLevel(updatedModule.getCourseLevel());
                    existingModule.setDuration(updatedModule.getDuration());
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
                    return trainingModuleRepository.save(existingModule);
                })
                .orElseThrow(() -> new RuntimeException("Training module not found with id: " + id));
    }

    @Override
    public void deleteTrainingModule(Long id) {
        trainingModuleRepository.deleteById(id);
    }

    @Override
    public void deactivateTrainingModule(Long id) {
        trainingModuleRepository.findById(id)
                .ifPresent(module -> {
                    module.setActive(false);
                    trainingModuleRepository.save(module);
                });
    }

    @Override
    public List<TrainingModule> getTrainingModulesByLevel(CourseLevel level) {
        return trainingModuleRepository.findByCourseLevelAndIsActiveTrue(level);
    }

    @Override
    public List<TrainingModule> getTrainingModulesByInstructor(String instructorName) {
        return trainingModuleRepository.findByInstructorNameContainingIgnoreCaseAndIsActiveTrue(instructorName);
    }

    @Override
    public List<TrainingModule> getAvailableForEnrollment() {
        return trainingModuleRepository.findCoursesWithAvailableSlots();
    }

    @Override
    public List<TrainingModule> getCertificationCourses() {
        return trainingModuleRepository.findByCertificationTrueAndIsActiveTrue();
    }

    @Override
    public List<TrainingModule> getTopRatedCourses() {
        return trainingModuleRepository.findTopRatedCourses();
    }

    @Override
    public List<TrainingModule> searchCourses(String keyword) {
        return trainingModuleRepository.searchByKeyword(keyword);
    }

    @Override
    public List<TrainingModule> getCoursesByTag(String tag) {
        return trainingModuleRepository.findByTagsContainingIgnoreCase(tag);
    }

    @Override
    public List<TrainingModule> getCoursesByMinRating(double minRating) {
        return trainingModuleRepository.findByRatingGreaterThanEqualAndIsActiveTrue(minRating);
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
    public TrainingModule updateRating(Long moduleId, double newRating, String comment) {
        return trainingModuleRepository.findById(moduleId)
                .map(module -> {
                    if (newRating < 0.0 || newRating > 5.0) {
                        throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
                    }

                    // Simple rating calculation (you might want to implement a more sophisticated algorithm)
                    double currentRating = module.getRating();
                    int enrolledCount = module.getEnrolledCount();

                    if (enrolledCount > 0) {
                        double newAverageRating = ((currentRating * enrolledCount) + newRating) / (enrolledCount + 1);
                        module.setRating(Math.round(newAverageRating * 10.0) / 10.0); // Round to 1 decimal place
                    } else {
                        module.setRating(newRating);
                    }

                    if (comment != null && !comment.trim().isEmpty()) {
                        module.getComments().add(comment);
                    }

                    return trainingModuleRepository.save(module);
                })
                .orElseThrow(() -> new RuntimeException("Training module not found with id: " + moduleId));
    }
}
