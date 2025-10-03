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
        return findCoursesWithAvailableSlots();
    }

    @Override
    public List<TrainingModule> getCertificationCourses() {
        return trainingModuleRepository.findByCertificationTrueAndIsActiveTrue();
    }

    @Override
    public List<TrainingModule> getTopRatedCourses() {
        return trainingModuleRepository.findByIsActiveTrueOrderByRatingDescEnrolledCountDesc();
    }

    @Override
    public List<TrainingModule> searchCourses(String keyword) {
        return searchByKeyword(keyword);
    }

    @Override
    public List<TrainingModule> getCoursesByTag(String tag) {
        return findByTagsContainingIgnoreCase(tag);
    }

    @Override
    public List<TrainingModule> getCoursesByMinRating(double minRating) {
        return trainingModuleRepository.findByRatingGreaterThanEqualAndIsActiveTrueOrderByRatingDesc(minRating);
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

    // Complex search methods implemented in service layer

    @Override
    public List<TrainingModule> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveTrainingModules();
        }

        String searchTerm = keyword.toLowerCase().trim();

        // Get all active training modules and filter in Java
        return getAllActiveTrainingModules().stream()
                .filter(module ->
                    (module.getCourseName() != null &&
                     module.getCourseName().toLowerCase().contains(searchTerm)) ||
                    (module.getDescription() != null &&
                     module.getDescription().toLowerCase().contains(searchTerm)) ||
                    (module.getInstructorName() != null &&
                     module.getInstructorName().toLowerCase().contains(searchTerm))
                )
                .toList();
    }

    @Override
    public List<TrainingModule> findCoursesWithAvailableSlots() {
        return getAllActiveTrainingModules().stream()
                .filter(module ->
                    module.isEnrollNowAvailable() &&
                    (module.getMaxEnrollment() == null ||
                     module.getEnrolledCount() < module.getMaxEnrollment())
                )
                .toList();
    }

    @Override
    public List<TrainingModule> findByTagsContainingIgnoreCase(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return List.of();
        }

        String searchTag = tag.toLowerCase().trim();

        return getAllActiveTrainingModules().stream()
                .filter(module ->
                    module.getTags() != null &&
                    module.getTags().stream()
                        .anyMatch(moduleTag ->
                            moduleTag.toLowerCase().contains(searchTag)
                        )
                )
                .toList();
    }

    @Override
    public List<TrainingModule> searchByMultipleCriteria(String keyword, CourseLevel level,
                                                        Double minRating, Double maxPrice,
                                                        Boolean certification) {
        List<TrainingModule> results = getAllActiveTrainingModules();

        // Apply keyword search
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchTerm = keyword.toLowerCase().trim();
            results = results.stream()
                    .filter(module ->
                            (module.getCourseName() != null &&
                             module.getCourseName().toLowerCase().contains(searchTerm)) ||
                            (module.getDescription() != null &&
                             module.getDescription().toLowerCase().contains(searchTerm)) ||
                            (module.getInstructorName() != null &&
                             module.getInstructorName().toLowerCase().contains(searchTerm))
                    )
                    .toList();
        }

        // Apply level filter
        if (level != null) {
            results = results.stream()
                    .filter(module -> module.getCourseLevel() == level)
                    .toList();
        }

        // Apply rating filter
        if (minRating != null) {
            results = results.stream()
                    .filter(module -> module.getRating() >= minRating)
                    .toList();
        }

        // Apply price filter
        if (maxPrice != null) {
            results = results.stream()
                    .filter(module -> module.getPrice() == null || module.getPrice() <= maxPrice)
                    .toList();
        }

        // Apply certification filter
        if (certification != null) {
            results = results.stream()
                    .filter(module -> module.isCertification() == certification)
                    .toList();
        }

        return results;
    }

    @Override
    public List<TrainingModule> getCoursesWithAvailableEnrollment() {
        return findCoursesWithAvailableSlots();
    }

    @Override
    public List<TrainingModule> getPopularCourses(int limit) {
        return getAllActiveTrainingModules().stream()
                .sorted((a, b) -> {
                    // First sort by rating (descending)
                    int ratingComparison = Double.compare(b.getRating(), a.getRating());
                    if (ratingComparison != 0) {
                        return ratingComparison;
                    }
                    // Then by enrolled count (descending)
                    return Integer.compare(b.getEnrolledCount(), a.getEnrolledCount());
                })
                .limit(limit)
                .toList();
    }

    @Override
    public List<TrainingModule> getRecentCourses(int limit) {
        return trainingModuleRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .toList();
    }
}
