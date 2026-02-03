package com.example.codebasebackend.dto;

import com.example.codebasebackend.Entities.DoctorStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {

    private Long id;
    private String doctorId;

    // Personal Information
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;

    // Contact
    private String email;
    private String phone;
    private String alternativePhone;

    // Professional Information
    private String licenseNumber;
    private Long specialtyId;
    private String specialtyName;
    private Integer experience;
    private List<String> qualifications;
    private List<String> languages;

    // Rating and Performance
    private Double rating;
    private Integer totalSessions;
    private Integer completedSessions;

    // Status and Availability
    private DoctorStatus status;
    private OffsetDateTime lastStatusUpdate;
    private Boolean active;

    // Profile
    private String photoUrl;
    private String bio;
    private String location;

    // Hospital Association
    private Long hospitalId;
    private String hospitalName;

    // Audit
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
