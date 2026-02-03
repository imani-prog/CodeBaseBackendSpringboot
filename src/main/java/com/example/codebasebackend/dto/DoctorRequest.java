package com.example.codebasebackend.dto;

import com.example.codebasebackend.Entities.DoctorStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$",
             message = "Invalid phone number format")
    private String phone;

    private String alternativePhone;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    private Long specialtyId;

    @Min(value = 0, message = "Experience must be non-negative")
    private Integer experience;

    private List<String> qualifications;

    private List<String> languages;

    private DoctorStatus status;

    private String photoUrl;

    private String bio;

    private String location;

    private Long hospitalId;

    private Boolean active;
}
