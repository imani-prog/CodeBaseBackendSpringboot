package com.example.codebasebackend.dto;

import com.example.codebasebackend.Entities.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemedicineSessionResponse {

    private Long id;
    private String sessionId;

    // Patient Information (enriched)
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;

    // Doctor Information (enriched)
    private Long doctorId;
    private String doctorName;
    private String doctorPhoto;
    private String doctorSpecialty;
    private Double doctorRating;

    // Hospital Information (optional)
    private Long hospitalId;
    private String hospitalName;

    // Session Details
    private SessionType sessionType;
    private PlatformType platform;
    private SessionStatus status;
    private Priority priority;

    // Timing
    private OffsetDateTime startTime;
    private OffsetDateTime actualStartTime;
    private OffsetDateTime endTime;
    private Integer duration; // actual duration in minutes
    private Integer plannedDuration;

    // Medical Information
    private List<String> symptoms;
    private String chiefComplaint;
    private String diagnosis;
    private String prescription;
    private String doctorNotes;
    private Boolean followUpRequired;
    private OffsetDateTime followUpDate;

    // Financial
    private BigDecimal cost;
    private BigDecimal actualCost;
    private String paymentStatus;
    private String paymentReference;

    // Quality
    private Integer rating;
    private String feedback;

    // Technical
    private String meetingLink;
    private String meetingId;
    private String recordingUrl;
    private Boolean recordingEnabled;

    // Metadata
    private String cancellationReason;
    private OffsetDateTime cancelledAt;
    private String cancelledByUserName;
    private Boolean reminderSent;
    private OffsetDateTime reminderSentAt;

    // Audit
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdByUserName;
}
