package com.example.codebasebackend.dto;

import com.example.codebasebackend.Entities.DoctorStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorOnlineResponse {

    private Long id;
    private String doctorId; // e.g., DOC-001
    private String name;
    private String photo;
    private String specialty;
    private Integer experience; // years
    private Double rating;

    private Integer sessionsToday;
    private Integer totalSessions;

    private DoctorStatus currentStatus;
    private OffsetDateTime nextAppointment;

    private Integer avgSessionDuration; // minutes
    private BigDecimal earnings; // today's earnings

    private List<String> languages;
    private String location;

    private String email;
    private String phone;
}
