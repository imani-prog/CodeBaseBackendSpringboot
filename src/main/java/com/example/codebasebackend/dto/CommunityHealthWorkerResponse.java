package com.example.codebasebackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class CommunityHealthWorkerResponse {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private String city;
    private String state;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long hospitalId;
    private String status;
    private String specialization;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

