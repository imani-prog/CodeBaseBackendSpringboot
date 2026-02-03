package com.example.codebasebackend.dto;

import com.example.codebasebackend.Entities.PlatformType;
import com.example.codebasebackend.Entities.Priority;
import com.example.codebasebackend.Entities.SessionType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemedicineSessionRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private Long hospitalId; // Optional

    @NotNull(message = "Session type is required")
    private SessionType sessionType;

    @NotNull(message = "Platform is required")
    private PlatformType platform;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Start time is required")
    private OffsetDateTime startTime;

    @Min(value = 5, message = "Planned duration must be at least 5 minutes")
    @Max(value = 180, message = "Planned duration cannot exceed 180 minutes")
    private Integer plannedDuration;

    @NotEmpty(message = "At least one symptom is required")
    private List<String> symptoms;

    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", message = "Cost must be non-negative")
    private BigDecimal cost;

    private Boolean recordingEnabled;
}
