package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.*;
import com.example.codebasebackend.dto.*;
import com.example.codebasebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TelemedicineSessionServiceImplementation implements TelemedicineSessionService {

    private final TelemedicineSessionRepository sessionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;

    @Override
    public TelemedicineSessionResponse createSession(TelemedicineSessionRequest request) {
        log.info("Creating telemedicine session for patient: {}, doctor: {}",
                 request.getPatientId(), request.getDoctorId());

        // Validate patient
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Patient not found with ID: " + request.getPatientId()));

        // Validate doctor
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Doctor not found with ID: " + request.getDoctorId()));

        // Validate doctor availability
        Integer activeSessions = sessionRepository.countActiveSessionsByDoctorId(request.getDoctorId());
        if (activeSessions != null && activeSessions >= 3) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Doctor is currently unavailable (max sessions reached)");
        }

        // Validate hospital if provided
        Hospital hospital = null;
        if (request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Hospital not found with ID: " + request.getHospitalId()));
        }

        // Generate unique session ID
        String sessionId = generateSessionId();

        // Create session entity using setters (no Lombok builder on entity)
        TelemedicineSession session = new TelemedicineSession();
        session.setSessionId(sessionId);
        session.setPatient(patient);
        session.setDoctor(doctor);
        session.setHospital(hospital);
        session.setSessionType(request.getSessionType());
        session.setPlatform(request.getPlatform());
        session.setPriority(request.getPriority());
        session.setStatus(SessionStatus.SCHEDULED);
        session.setStartTime(request.getStartTime());
        session.setPlannedDuration(request.getPlannedDuration());
        session.setSymptoms(request.getSymptoms() != null ? request.getSymptoms() : new ArrayList<>());
        session.setChiefComplaint(request.getChiefComplaint());
        session.setCost(request.getCost());
        session.setActualCost(request.getCost());
        session.setPaymentStatus("PENDING");
        session.setRecordingEnabled(request.getRecordingEnabled() != null ? request.getRecordingEnabled() : false);
        session.setReminderSent(false);
        session.setFollowUpRequired(false);

        // Generate meeting link
        session.setMeetingLink(generateMeetingLink(sessionId));
        session.setMeetingId(UUID.randomUUID().toString());

        TelemedicineSession savedSession = sessionRepository.save(session);
        log.info("Telemedicine session created successfully: {}", sessionId);

        return mapToResponse(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public TelemedicineSessionResponse getSessionById(Long id) {
        TelemedicineSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Session not found with ID: " + id));
        return mapToResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public TelemedicineSessionResponse getSessionBySessionId(String sessionId) {
        TelemedicineSession session = sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Session not found with ID: " + sessionId));
        return mapToResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TelemedicineSessionResponse> getAllSessions(Pageable pageable) {
        return sessionRepository.findAll(pageable)
            .map(this::mapToResponse);
    }

    @Override
    public TelemedicineSessionResponse updateSession(Long id, TelemedicineSessionRequest request) {
        TelemedicineSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Session not found with ID: " + id));

        // Only allow updates for scheduled sessions
        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Only scheduled sessions can be updated");
        }

        // Update fields
        session.setStartTime(request.getStartTime());
        session.setPlannedDuration(request.getPlannedDuration());
        session.setSymptoms(request.getSymptoms());
        session.setChiefComplaint(request.getChiefComplaint());
        session.setCost(request.getCost());
        session.setActualCost(request.getCost());
        session.setPriority(request.getPriority());

        TelemedicineSession updatedSession = sessionRepository.save(session);
        return mapToResponse(updatedSession);
    }

    @Override
    public void deleteSession(Long id) {
        TelemedicineSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Session not found with ID: " + id));

        // Only allow deletion of scheduled or cancelled sessions
        if (session.getStatus() != SessionStatus.SCHEDULED &&
            session.getStatus() != SessionStatus.CANCELLED) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Only scheduled or cancelled sessions can be deleted");
        }

        sessionRepository.delete(session);
        log.info("Session deleted: {}", session.getSessionId());
    }

    @Override
    public TelemedicineSessionResponse startSession(Long id) {
        TelemedicineSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Session not found with ID: " + id));

        if (session.getStatus() != SessionStatus.SCHEDULED &&
            session.getStatus() != SessionStatus.PAUSED) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Session cannot be started in current status: " + session.getStatus());
        }

        session.startSession();
        TelemedicineSession updatedSession = sessionRepository.save(session);
        log.info("Session started: {}", session.getSessionId());

        return mapToResponse(updatedSession);
    }

    @Override
    public TelemedicineSessionResponse pauseSession(Long id) {
        TelemedicineSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Session not found with ID: " + id));

        session.pauseSession();
        TelemedicineSession updatedSession = sessionRepository.save(session);
        log.info("Session paused: {}", session.getSessionId());

        return mapToResponse(updatedSession);
    }

    @Override
    public TelemedicineSessionResponse resumeSession(Long id) {
        TelemedicineSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Session not found with ID: " + id));

        session.resumeSession();
        TelemedicineSession updatedSession = sessionRepository.save(session);
        log.info("Session resumed: {}", session.getSessionId());

        return mapToResponse(updatedSession);
    }

    @Override
    public TelemedicineSessionResponse completeSession(Long id, String diagnosis,
                                                       String prescription, String doctorNotes) {
        TelemedicineSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Session not found with ID: " + id));

        if (session.getStatus() != SessionStatus.ACTIVE &&
            session.getStatus() != SessionStatus.PAUSED) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Only active or paused sessions can be completed");
        }

        session.completeSession();
        session.setDiagnosis(diagnosis);
        session.setPrescription(prescription);
        session.setDoctorNotes(doctorNotes);
        session.setPaymentStatus("PAID");

        TelemedicineSession updatedSession = sessionRepository.save(session);
        log.info("Session completed: {}", session.getSessionId());

        return mapToResponse(updatedSession);
    }

    @Override
    public TelemedicineSessionResponse cancelSession(Long id, String reason) {
        TelemedicineSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Session not found with ID: " + id));

        if (session.getStatus() == SessionStatus.COMPLETED ||
            session.getStatus() == SessionStatus.CANCELLED) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Cannot cancel session in status: " + session.getStatus());
        }

        session.cancelSession(null, reason);
        TelemedicineSession updatedSession = sessionRepository.save(session);
        log.info("Session cancelled: {}", session.getSessionId());

        return mapToResponse(updatedSession);
    }

    @Override
    public TelemedicineSessionResponse terminateSession(Long id, String reason) {
        TelemedicineSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Session not found with ID: " + id));

        session.terminateSession(null, reason);
        TelemedicineSession updatedSession = sessionRepository.save(session);
        log.info("Session terminated: {}", session.getSessionId());

        return mapToResponse(updatedSession);
    }

    @Override
    public TelemedicineSessionResponse rateSession(Long id, Integer rating, String feedback) {
        TelemedicineSession session = sessionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Session not found with ID: " + id));

        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Only completed sessions can be rated");
        }

        session.setRating(rating);
        session.setFeedback(feedback);
        TelemedicineSession updatedSession = sessionRepository.save(session);
        log.info("Session rated: {}, rating: {}", session.getSessionId(), rating);

        return mapToResponse(updatedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TelemedicineSessionResponse> getSessionsByStatus(SessionStatus status, Pageable pageable) {
        return sessionRepository.findByStatus(status, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TelemedicineSessionResponse> getSessionsByPlatform(PlatformType platform, Pageable pageable) {
        return sessionRepository.findByPlatform(platform, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TelemedicineSessionResponse> getActiveSessionsByPriority(Priority priority) {
        return sessionRepository.findByPriorityOrderByStartTimeAsc(priority)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TelemedicineSessionResponse> searchSessions(String searchTerm, Pageable pageable) {
        return sessionRepository.searchSessions(searchTerm, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TelemedicineSessionResponse> getSessionsWithFilters(
            SessionStatus status, PlatformType platform, Priority priority,
            Long doctorId, OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable) {
        return sessionRepository.findWithFilters(status, platform, priority,
                doctorId, startDate, endDate, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TelemedicineSessionResponse> getSessionsByPatient(Long patientId, Pageable pageable) {
        return sessionRepository.findByPatientId(patientId, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TelemedicineSessionResponse> getSessionsByDoctor(Long doctorId, Pageable pageable) {
        return sessionRepository.findByDoctorId(doctorId, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TelemedicineSessionResponse> getActiveDoctorSessions(Long doctorId) {
        return sessionRepository.findByDoctorIdAndStatus(doctorId, SessionStatus.ACTIVE)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlatformOverviewResponse getPlatformOverview() {
        Integer activeSessions = sessionRepository.countByStatus(SessionStatus.ACTIVE);
        Long totalSessionsLong = sessionRepository.count();
        Integer totalSessions = totalSessionsLong.intValue();

        Integer activeDoctors = sessionRepository.countActiveDoctors();
        Long totalDoctorsLong = doctorRepository.count();
        Integer totalDoctors = totalDoctorsLong.intValue();

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime monthStart = now.minusMonths(1);
        BigDecimal totalRevenue = sessionRepository.calculateRevenue(monthStart, now);

        Double avgDuration = sessionRepository.calculateAverageDuration();
        Double avgRating = sessionRepository.calculateAverageRating();

        // Calculate growth
        OffsetDateTime previousMonthStart = monthStart.minusMonths(1);
        BigDecimal previousRevenue = sessionRepository.calculateRevenue(previousMonthStart, monthStart);
        Double monthlyGrowth = calculateGrowthPercentage(previousRevenue, totalRevenue);

        return PlatformOverviewResponse.builder()
            .totalSessions(totalSessions)
            .activeSessions(activeSessions != null ? activeSessions : 0)
            .totalDoctors(totalDoctors)
            .onlineDoctors(activeDoctors != null ? activeDoctors : 0)
            .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
            .monthlyGrowth(monthlyGrowth)
            .avgSessionDuration(avgDuration != null ? avgDuration.intValue() : 0)
            .patientSatisfaction(avgRating != null ? avgRating : 0.0)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueDataResponse getRevenueData(String period) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfDay = now.toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime();
        OffsetDateTime startOfWeek = now.minusWeeks(1);
        OffsetDateTime startOfMonth = now.minusMonths(1);

        BigDecimal dailyRevenue = sessionRepository.calculateRevenue(startOfDay, now);
        BigDecimal weeklyRevenue = sessionRepository.calculateRevenue(startOfWeek, now);
        BigDecimal monthlyRevenue = sessionRepository.calculateRevenue(startOfMonth, now);

        // Revenue by specialty
        List<Object[]> specialtyData = sessionRepository.findRevenueBySpecialty();
        List<RevenueDataResponse.RevenueBySpecialty> bySpecialty = specialtyData.stream()
            .map(row -> RevenueDataResponse.RevenueBySpecialty.builder()
                .specialty((String) row[0])
                .revenue((BigDecimal) row[1])
                .sessions(((Number) row[2]).intValue())
                .avgCost((BigDecimal) row[3])
                .build())
            .collect(Collectors.toList());

        return RevenueDataResponse.builder()
            .daily(dailyRevenue != null ? dailyRevenue : BigDecimal.ZERO)
            .weekly(weeklyRevenue != null ? weeklyRevenue : BigDecimal.ZERO)
            .monthly(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO)
            .bySpecialty(bySpecialty)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PlatformStatsResponse getPlatformStats() {
        List<Object[]> stats = sessionRepository.findPlatformStatistics();

        Map<PlatformType, PlatformStatsResponse.PlatformUsage> usageByPlatform = new HashMap<>();
        int totalSessions = stats.stream()
            .mapToInt(row -> ((Number) row[1]).intValue())
            .sum();

        for (Object[] row : stats) {
            PlatformType platform = (PlatformType) row[0];
            Integer sessions = ((Number) row[1]).intValue();
            Double avgDuration = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            Double percentage = totalSessions > 0 ? (sessions * 100.0 / totalSessions) : 0.0;

            usageByPlatform.put(platform, PlatformStatsResponse.PlatformUsage.builder()
                .sessions(sessions)
                .percentage(percentage)
                .avgDuration(avgDuration.intValue())
                .build());
        }

        return PlatformStatsResponse.builder()
            .usageByPlatform(usageByPlatform)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorOnlineResponse> getOnlineDoctors() {
        List<DoctorStatus> onlineStatuses = Arrays.asList(DoctorStatus.AVAILABLE, DoctorStatus.BUSY);
        List<Doctor> onlineDoctors = doctorRepository.findByActiveTrueAndStatusIn(onlineStatuses);

        return onlineDoctors.stream()
            .map(doctor -> DoctorOnlineResponse.builder()
                .id(doctor.getId())
                .doctorId(doctor.getDoctorId())
                .name(doctor.getFullName())
                .photo(doctor.getPhotoUrl())
                .specialty(doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : "General")
                .experience(doctor.getExperience())
                .rating(doctor.getRating())
                .sessionsToday(0) // Can be calculated from sessions
                .totalSessions(doctor.getTotalSessions())
                .currentStatus(doctor.getStatus())
                .languages(doctor.getLanguages())
                .location(doctor.getLocation())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionHistoryResponse> getSessionHistory(String period) {
        OffsetDateTime startDate;
        OffsetDateTime now = OffsetDateTime.now();

        switch (period.toLowerCase()) {
            case "today":
                startDate = now.toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime();
                break;
            case "week":
                startDate = now.minusWeeks(1);
                break;
            case "month":
                startDate = now.minusMonths(1);
                break;
            default:
                startDate = now.minusDays(1);
        }

        List<TelemedicineSession> sessions = sessionRepository.findByDateRange(startDate, now);

        return sessions.stream()
            .map(session -> SessionHistoryResponse.builder()
                .id(session.getSessionId())
                .patient(session.getPatient().getFirstName() + " " + session.getPatient().getLastName())
                .doctor(session.getDoctor().getFullName())
                .date(session.getStartTime().toLocalDate())
                .duration(session.getDuration())
                .status(session.getStatus())
                .rating(session.getRating())
                .cost(session.getActualCost())
                .diagnosis(session.getDiagnosis())
                .followUpRequired(session.getFollowUpRequired())
                .prescription(session.getPrescription())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public void sendSessionReminders() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime reminderWindow = now.plusHours(24);

        List<TelemedicineSession> sessionsNeedingReminders =
            sessionRepository.findSessionsNeedingReminders(now, reminderWindow);

        for (TelemedicineSession session : sessionsNeedingReminders) {
            // Here you would integrate with email/SMS service
            log.info("Sending reminder for session: {} to patient: {}",
                     session.getSessionId(), session.getPatient().getEmail());

            session.setReminderSent(true);
            session.setReminderSentAt(now);
            sessionRepository.save(session);
        }

        log.info("Sent reminders for {} sessions", sessionsNeedingReminders.size());
    }

    // Helper Methods
    private String generateSessionId() {
        long count = sessionRepository.count() + 1;
        return String.format("TM-%03d", count);
    }

    private String generateMeetingLink(String sessionId) {
        return "https://telemedicine.medilink.com/session/" + sessionId;
    }

    private Double calculateGrowthPercentage(BigDecimal previous, BigDecimal current) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        if (current == null) {
            return 0.0;
        }
        BigDecimal growth = current.subtract(previous)
            .divide(previous, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
        return growth.doubleValue();
    }

    private TelemedicineSessionResponse mapToResponse(TelemedicineSession session) {
        return TelemedicineSessionResponse.builder()
            .id(session.getId())
            .sessionId(session.getSessionId())
            // Patient info
            .patientId(session.getPatient().getId())
            .patientName(session.getPatient().getFirstName() + " " + session.getPatient().getLastName())
            .patientEmail(session.getPatient().getEmail())
            .patientPhone(session.getPatient().getPhone())
            // Doctor info
            .doctorId(session.getDoctor().getId())
            .doctorName(session.getDoctor().getFullName())
            .doctorPhoto(session.getDoctor().getPhotoUrl())
            .doctorSpecialty(session.getDoctor().getSpecialty() != null ?
                session.getDoctor().getSpecialty().getName() : "General")
            .doctorRating(session.getDoctor().getRating())
            // Hospital info
            .hospitalId(session.getHospital() != null ? session.getHospital().getId() : null)
            .hospitalName(session.getHospital() != null ? session.getHospital().getName() : null)
            // Session details
            .sessionType(session.getSessionType())
            .platform(session.getPlatform())
            .status(session.getStatus())
            .priority(session.getPriority())
            // Timing
            .startTime(session.getStartTime())
            .actualStartTime(session.getActualStartTime())
            .endTime(session.getEndTime())
            .duration(session.getDuration())
            .plannedDuration(session.getPlannedDuration())
            // Medical
            .symptoms(session.getSymptoms())
            .chiefComplaint(session.getChiefComplaint())
            .diagnosis(session.getDiagnosis())
            .prescription(session.getPrescription())
            .doctorNotes(session.getDoctorNotes())
            .followUpRequired(session.getFollowUpRequired())
            .followUpDate(session.getFollowUpDate())
            // Financial
            .cost(session.getCost())
            .actualCost(session.getActualCost())
            .paymentStatus(session.getPaymentStatus())
            .paymentReference(session.getPaymentReference())
            // Quality
            .rating(session.getRating())
            .feedback(session.getFeedback())
            // Technical
            .meetingLink(session.getMeetingLink())
            .meetingId(session.getMeetingId())
            .recordingUrl(session.getRecordingUrl())
            .recordingEnabled(session.getRecordingEnabled())
            // Metadata
            .cancellationReason(session.getCancellationReason())
            .cancelledAt(session.getCancelledAt())
            .cancelledByUserName(session.getCancelledBy() != null ?
                session.getCancelledBy().getFullName() : null)
            .reminderSent(session.getReminderSent())
            .reminderSentAt(session.getReminderSentAt())
            // Audit
            .createdAt(session.getCreatedAt())
            .updatedAt(session.getUpdatedAt())
            .createdByUserName(session.getCreatedBy() != null ?
                session.getCreatedBy().getFullName() : null)
            .build();
    }
}
