package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.AuditLog;
import com.example.codebasebackend.Entities.TrainingModule;
import com.example.codebasebackend.Entities.TrainingModule.CourseLevel;
import com.example.codebasebackend.configs.Auditable;
import com.example.codebasebackend.services.TrainingModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-modules")
@RequiredArgsConstructor
public class TrainingModuleController {

    private final TrainingModuleService trainingModuleService;

    // Create training module
    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "TrainingModule", entityIdExpression = "#result.body.id", includeArgs = true, includeResult = true)
    @PostMapping
    public ResponseEntity<TrainingModule> createTrainingModule(@Valid @RequestBody TrainingModule trainingModule) {
        TrainingModule saved = trainingModuleService.createTrainingModule(trainingModule);
        return ResponseEntity.ok(saved);
    }

    // List all training modules
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "TrainingModule", includeArgs = true)
    @GetMapping
    public ResponseEntity<List<TrainingModule>> listTrainingModules() {
        return ResponseEntity.ok(trainingModuleService.getAllTrainingModules());
    }

    // Get training module by id
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "TrainingModule", entityIdExpression = "#id", includeArgs = true)
    @GetMapping("/{id}")
    public ResponseEntity<TrainingModule> getTrainingModule(@PathVariable Long id) {
        return ResponseEntity.ok(trainingModuleService.getTrainingModuleById(id));
    }


//    @Auditable(eventType = AuditLog.EventType.CREATE, entityType = "TrainingModule", entityIdExpression = "#result.body.id", includeArgs = true, includeResult = true)
//    @PostMapping
//    public ResponseEntity<TrainingModule> createTrainingModule(@Valid @RequestBody TrainingModule trainingModule) {
//        TrainingModule saved = trainingModuleService.createTrainingModule(trainingModule);
//        return ResponseEntity.ok(saved);
//    }

    // Update training module
    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "TrainingModule", entityIdExpression = "#id", includeArgs = true, includeResult = true)
    @PutMapping("/{id}")
    public ResponseEntity<TrainingModule> updateTrainingModule(
            @PathVariable Long id,
            @Valid @RequestBody TrainingModule trainingModule) {
        TrainingModule updated = trainingModuleService.updateTrainingModule(id, trainingModule);
        return ResponseEntity.ok(updated);
    }

    // Delete training module
    @Auditable(eventType = AuditLog.EventType.DELETE, entityType = "TrainingModule", entityIdExpression = "#id", includeArgs = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainingModule(@PathVariable Long id) {
        trainingModuleService.deleteTrainingModule(id);
        return ResponseEntity.noContent().build();
    }

    // Deactivate training module (soft delete)
    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "TrainingModule", entityIdExpression = "#id", includeArgs = true)
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateTrainingModule(@PathVariable Long id) {
        trainingModuleService.deactivateTrainingModule(id);
        return ResponseEntity.noContent().build();
    }

    // Get active training modules
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "TrainingModule", includeArgs = true)
    @GetMapping("/active")
    public ResponseEntity<List<TrainingModule>> getActiveTrainingModules() {
        return ResponseEntity.ok(trainingModuleService.getAllActiveTrainingModules());
    }

    // Get training modules by course level
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "TrainingModule", includeArgs = true)
    @GetMapping("/level/{level}")
    public ResponseEntity<List<TrainingModule>> getTrainingModulesByLevel(@PathVariable CourseLevel level) {
        return ResponseEntity.ok(trainingModuleService.getTrainingModulesByLevel(level));
    }

    // Get training modules by instructor
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "TrainingModule", includeArgs = true)
    @GetMapping("/instructor/{instructorName}")
    public ResponseEntity<List<TrainingModule>> getTrainingModulesByInstructor(@PathVariable String instructorName) {
        return ResponseEntity.ok(trainingModuleService.getTrainingModulesByInstructor(instructorName));
    }

    // Get certification courses
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "TrainingModule", includeArgs = true)
    @GetMapping("/certification")
    public ResponseEntity<List<TrainingModule>> getCertificationCourses() {
        return ResponseEntity.ok(trainingModuleService.getCertificationCourses());
    }

    // Get top rated courses
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "TrainingModule", includeArgs = true)
    @GetMapping("/top-rated")
    public ResponseEntity<List<TrainingModule>> getTopRatedCourses() {
        return ResponseEntity.ok(trainingModuleService.getTopRatedCourses());
    }

    // Get courses by minimum rating
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "TrainingModule", includeArgs = true)
    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<TrainingModule>> getCoursesByMinRating(@PathVariable double minRating) {
        return ResponseEntity.ok(trainingModuleService.getCoursesByMinRating(minRating));
    }

    // Get courses with available slots
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "TrainingModule", includeArgs = true)
    @GetMapping("/available")
    public ResponseEntity<List<TrainingModule>> getCoursesWithAvailableSlots() {
        return ResponseEntity.ok(trainingModuleService.getCoursesWithAvailableSlots());
    }

    // Search courses by keyword
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "TrainingModule", includeArgs = true)
    @GetMapping("/search")
    public ResponseEntity<List<TrainingModule>> searchCourses(@RequestParam String keyword) {
        return ResponseEntity.ok(trainingModuleService.searchCourses(keyword));
    }

    // Get courses by tag
    @Auditable(eventType = AuditLog.EventType.READ, entityType = "TrainingModule", includeArgs = true)
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<TrainingModule>> getCoursesByTag(@PathVariable String tag) {
        return ResponseEntity.ok(trainingModuleService.getCoursesByTag(tag));
    }

    // Enroll a student in a course
    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "TrainingModule", entityIdExpression = "#id", includeArgs = true, includeResult = true)
    @PostMapping("/{id}/enroll")
    public ResponseEntity<TrainingModule> enrollStudent(@PathVariable Long id) {
        TrainingModule enrolled = trainingModuleService.enrollStudent(id);
        return ResponseEntity.ok(enrolled);
    }

    // Add rating and comment to a course
    @Auditable(eventType = AuditLog.EventType.UPDATE, entityType = "TrainingModule", entityIdExpression = "#id", includeArgs = true, includeResult = true)
    @PostMapping("/{id}/rate")
    public ResponseEntity<TrainingModule> addRating(
            @PathVariable Long id,
            @RequestParam double rating,
            @RequestParam(required = false) String comment) {
        TrainingModule rated = trainingModuleService.addRating(id, rating, comment);
        return ResponseEntity.ok(rated);
    }
}