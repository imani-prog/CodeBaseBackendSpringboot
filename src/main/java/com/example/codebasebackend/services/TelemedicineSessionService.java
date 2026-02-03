package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.*;
import com.example.codebasebackend.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;

public interface TelemedicineSessionService {

    // CRUD Operations
    TelemedicineSessionResponse createSession(TelemedicineSessionRequest request);
    TelemedicineSessionResponse getSessionById(Long id);
    TelemedicineSessionResponse getSessionBySessionId(String sessionId);
    Page<TelemedicineSessionResponse> getAllSessions(Pageable pageable);
    TelemedicineSessionResponse updateSession(Long id, TelemedicineSessionRequest request);
    void deleteSession(Long id);

    // Session Control Operations
    TelemedicineSessionResponse startSession(Long id);
    TelemedicineSessionResponse pauseSession(Long id);
    TelemedicineSessionResponse resumeSession(Long id);
    TelemedicineSessionResponse completeSession(Long id, String diagnosis, String prescription, String doctorNotes);
    TelemedicineSessionResponse cancelSession(Long id, String reason);
    TelemedicineSessionResponse terminateSession(Long id, String reason);

    // Rating and Feedback
    TelemedicineSessionResponse rateSession(Long id, Integer rating, String feedback);

    // Filtering and Search
    Page<TelemedicineSessionResponse> getSessionsByStatus(SessionStatus status, Pageable pageable);
    Page<TelemedicineSessionResponse> getSessionsByPlatform(PlatformType platform, Pageable pageable);
    List<TelemedicineSessionResponse> getActiveSessionsByPriority(Priority priority);
    Page<TelemedicineSessionResponse> searchSessions(String searchTerm, Pageable pageable);
    Page<TelemedicineSessionResponse> getSessionsWithFilters(
        SessionStatus status,
        PlatformType platform,
        Priority priority,
        Long doctorId,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        Pageable pageable
    );

    // Patient and Doctor specific
    Page<TelemedicineSessionResponse> getSessionsByPatient(Long patientId, Pageable pageable);
    Page<TelemedicineSessionResponse> getSessionsByDoctor(Long doctorId, Pageable pageable);
    List<TelemedicineSessionResponse> getActiveDoctorSessions(Long doctorId);

    // Analytics and Statistics
    PlatformOverviewResponse getPlatformOverview();
    RevenueDataResponse getRevenueData(String period); // "daily", "weekly", "monthly"
    PlatformStatsResponse getPlatformStats();
    List<DoctorOnlineResponse> getOnlineDoctors();
    List<SessionHistoryResponse> getSessionHistory(String period);

    // Reminders
    void sendSessionReminders();
}
