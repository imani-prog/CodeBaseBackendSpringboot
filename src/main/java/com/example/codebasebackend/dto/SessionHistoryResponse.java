package com.example.codebasebackend.dto;

import com.example.codebasebackend.Entities.SessionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionHistoryResponse {

    private String id; // Session ID (e.g., TM-H001)
    private String patient;
    private String doctor;
    private LocalDate date;
    private Integer duration;
    private SessionStatus status;
    private Integer rating;
    private BigDecimal cost;
    private String diagnosis;
    private Boolean followUpRequired;
    private String prescription;
}
