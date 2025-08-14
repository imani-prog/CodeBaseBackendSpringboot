package com.example.codebasebackend.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AssistanceResponse {
    public enum Mode { AMBULANCE, CHW }

    private Mode mode;

    private String incidentId; // if AMBULANCE
    private Long assignmentId; // if CHW

    private Long patientId;
    private Long hospitalId;
    private Long chwId;

    private String priority;
    private String status; // dispatch status or assignment status

    private OffsetDateTime createdAt;
}

