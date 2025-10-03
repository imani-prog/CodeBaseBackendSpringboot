package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "training_modules")
public class TrainingModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course name is required")
    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    @NotBlank(message = "Course level is required")
    @Column(name = "course_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseLevel courseLevel;

    @NotBlank(message = "Duration is required")
    @Column(name = "duration", nullable = false, length = 100)
    private String duration;

    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    @Column(name = "rating", columnDefinition = "DECIMAL(2,1) DEFAULT 0.0")
    @Builder.Default
    private double rating = 0.0;

    @ElementCollection
    @CollectionTable(name = "training_module_comments", joinColumns = @JoinColumn(name = "training_module_id"))
    @Column(name = "comment", length = 1000)
    @Builder.Default
    private List<String> comments = new ArrayList<>();

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "brochure_url", length = 500)
    private String brochureUrl;

    @Column(name = "certification", nullable = false)
    @Builder.Default
    private boolean certification = false;

    @ElementCollection
    @CollectionTable(name = "training_module_modules", joinColumns = @JoinColumn(name = "training_module_id"))
    @Column(name = "module_name", length = 200)
    @Size(min = 5, message = "Course must have at least 5 modules")
    @Builder.Default
    private List<String> courseModules = new ArrayList<>();

    @Column(name = "enroll_now_available", nullable = false)
    @Builder.Default
    private boolean enrollNowAvailable = true;

    @Column(name = "enrolled_count", nullable = false)
    @Min(value = 0, message = "Enrolled count cannot be negative")
    @Builder.Default
    private int enrolledCount = 0;

    @Column(name = "prerequisites", length = 1000)
    private String prerequisites;

    @Column(name = "instructor_name", length = 200)
    private String instructorName;

    @ElementCollection
    @CollectionTable(name = "training_module_tags", joinColumns = @JoinColumn(name = "training_module_id"))
    @Column(name = "tag", length = 100)
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "price", columnDefinition = "DECIMAL(10,2)")
    private Double price;

    @Column(name = "max_enrollment")
    private Integer maxEnrollment;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enum for Course Level
    public enum CourseLevel {
        BEGINNER("Beginner"),
        INTERMEDIATE("Intermediate"),
        PROFESSIONAL("Professional");

        private final String displayName;

        CourseLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
