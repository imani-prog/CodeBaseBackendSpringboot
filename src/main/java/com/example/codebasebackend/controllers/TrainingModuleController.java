package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.TrainingModule;
import com.example.codebasebackend.Entities.TrainingModule.CourseLevel;
import com.example.codebasebackend.services.TrainingModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/training-modules")
@CrossOrigin(origins = "*")
public class TrainingModuleController {

    @Autowired
    private TrainingModuleService trainingModuleService;

    @PostMapping
    public ResponseEntity<TrainingModule> createTrainingModule(@Valid @RequestBody TrainingModule trainingModule) {
        try {
            TrainingModule createdModule = trainingModuleService.createTrainingModule(trainingModule);
            return new ResponseEntity<>(createdModule, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingModule> getTrainingModuleById(@PathVariable Long id) {
        Optional<TrainingModule> module = trainingModuleService.getTrainingModuleById(id);
        return module.map(trainingModule -> ResponseEntity.ok().body(trainingModule))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<TrainingModule>> getAllTrainingModules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TrainingModule> modules = trainingModuleService.getAllTrainingModules(pageable);
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/active")
    public ResponseEntity<List<TrainingModule>> getAllActiveTrainingModules() {
        List<TrainingModule> modules = trainingModuleService.getAllActiveTrainingModules();
        return ResponseEntity.ok(modules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingModule> updateTrainingModule(
            @PathVariable Long id,
            @Valid @RequestBody TrainingModule trainingModule) {
        try {
            TrainingModule updatedModule = trainingModuleService.updateTrainingModule(id, trainingModule);
            return ResponseEntity.ok(updatedModule);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainingModule(@PathVariable Long id) {
        trainingModuleService.deleteTrainingModule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateTrainingModule(@PathVariable Long id) {
        trainingModuleService.deactivateTrainingModule(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<TrainingModule>> getTrainingModulesByLevel(@PathVariable CourseLevel level) {
        List<TrainingModule> modules = trainingModuleService.getTrainingModulesByLevel(level);
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/instructor/{instructorName}")
    public ResponseEntity<List<TrainingModule>> getTrainingModulesByInstructor(@PathVariable String instructorName) {
        List<TrainingModule> modules = trainingModuleService.getTrainingModulesByInstructor(instructorName);
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/available-for-enrollment")
    public ResponseEntity<List<TrainingModule>> getAvailableForEnrollment() {
        List<TrainingModule> modules = trainingModuleService.getAvailableForEnrollment();
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/certification")
    public ResponseEntity<List<TrainingModule>> getCertificationCourses() {
        List<TrainingModule> modules = trainingModuleService.getCertificationCourses();
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<TrainingModule>> getTopRatedCourses() {
        List<TrainingModule> modules = trainingModuleService.getTopRatedCourses();
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrainingModule>> searchCourses(@RequestParam String keyword) {
        List<TrainingModule> modules = trainingModuleService.searchCourses(keyword);
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<TrainingModule>> getCoursesByTag(@PathVariable String tag) {
        List<TrainingModule> modules = trainingModuleService.getCoursesByTag(tag);
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/min-rating/{minRating}")
    public ResponseEntity<List<TrainingModule>> getCoursesByMinRating(@PathVariable double minRating) {
        List<TrainingModule> modules = trainingModuleService.getCoursesByMinRating(minRating);
        return ResponseEntity.ok(modules);
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<TrainingModule> enrollStudent(@PathVariable Long id) {
        try {
            TrainingModule module = trainingModuleService.enrollStudent(id);
            return ResponseEntity.ok(module);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<TrainingModule> rateModule(
            @PathVariable Long id,
            @RequestBody Map<String, Object> ratingData) {
        try {
            double rating = ((Number) ratingData.get("rating")).doubleValue();
            String comment = (String) ratingData.get("comment");

            TrainingModule module = trainingModuleService.updateRating(id, rating, comment);
            return ResponseEntity.ok(module);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/course-levels")
    public ResponseEntity<CourseLevel[]> getCourseLevels() {
        return ResponseEntity.ok(CourseLevel.values());
    }
}
