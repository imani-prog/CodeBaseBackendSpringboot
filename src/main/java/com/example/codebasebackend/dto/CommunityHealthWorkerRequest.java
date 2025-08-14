package com.example.codebasebackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CommunityHealthWorkerRequest {
    @NotBlank
    private String firstName;
    private String middleName;
    @NotBlank
    private String lastName;
    @Email
    private String email;
    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    private Long hospitalId;
    private String specialization;
}

