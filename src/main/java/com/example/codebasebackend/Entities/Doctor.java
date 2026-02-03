package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "doctors",
        indexes = {
                @Index(name = "idx_doctor_last_name", columnList = "lastName"),
                @Index(name = "idx_doctor_email", columnList = "email"),
                @Index(name = "idx_doctor_license", columnList = "licenseNumber"),
                @Index(name = "idx_doctor_specialty", columnList = "specialty_id"),
                @Index(name = "idx_doctor_status", columnList = "status"),
                @Index(name = "idx_doctor_hospital", columnList = "hospital_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_doctor_email", columnNames = {"email"}),
                @UniqueConstraint(name = "uk_doctor_license", columnNames = {"licenseNumber"})
        }
)
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 16, unique = true)
    private String doctorId; // DOC-001, DOC-002 format

    // Identity
    @NotBlank(message = "First name is required")
    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(length = 100)
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Column(nullable = false, length = 100)
    private String lastName;

    // Contact
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$",
             message = "Invalid phone number format")
    @Column(length = 20)
    private String phone;

    @Column(length = 20)
    private String alternativePhone;

    // Professional Information
    @NotBlank(message = "License number is required")
    @Column(nullable = false, length = 50, unique = true)
    private String licenseNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

    @Min(value = 0, message = "Experience must be non-negative")
    @Column
    private Integer experience; // years of experience

    @ElementCollection
    @CollectionTable(name = "doctor_qualifications", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "qualification", length = 200)
    private List<String> qualifications = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "doctor_languages", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "language", length = 50)
    private List<String> languages = new ArrayList<>();

    // Rating and Performance
    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    @Column(columnDefinition = "DECIMAL(2,1)")
    private Double rating = 0.0;

    @Min(value = 0)
    @Column
    private Integer totalSessions = 0;

    @Min(value = 0)
    @Column
    private Integer completedSessions = 0;

    // Status and Availability
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DoctorStatus status = DoctorStatus.OFFLINE;

    @Column
    private OffsetDateTime lastStatusUpdate;

    @Column(nullable = false)
    private Boolean active = true;

    // Profile
    @Column(length = 500)
    private String photoUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String location;

    // Hospital Association (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    // Audit Fields
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // Helper method to get full name
    public String getFullName() {
        StringBuilder fullName = new StringBuilder(firstName);
        if (middleName != null && !middleName.isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        fullName.append(" ").append(lastName);
        return fullName.toString();
    }

    // Helper method to update rating
    public void updateRating(Integer newRating) {
        if (this.rating == null || this.rating == 0.0) {
            this.rating = newRating.doubleValue();
        } else {
            // Calculate weighted average
            int totalRatedSessions = this.completedSessions;
            double totalRatingPoints = this.rating * totalRatedSessions;
            this.rating = (totalRatingPoints + newRating) / (totalRatedSessions + 1);
        }
    }

    // Update status
    public void updateStatus(DoctorStatus newStatus) {
        this.status = newStatus;
        this.lastStatusUpdate = OffsetDateTime.now();
    }

    // Increment sessions
    public void incrementTotalSessions() {
        this.totalSessions = (this.totalSessions == null ? 0 : this.totalSessions) + 1;
    }

    public void incrementCompletedSessions() {
        this.completedSessions = (this.completedSessions == null ? 0 : this.completedSessions) + 1;
    }
}
