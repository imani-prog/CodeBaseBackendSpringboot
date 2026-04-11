package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Appointment;
import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.Doctor;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.Entities.PlatformType;
import com.example.codebasebackend.Entities.Priority;
import com.example.codebasebackend.Entities.SessionStatus;
import com.example.codebasebackend.Entities.SessionType;
import com.example.codebasebackend.Entities.TelemedicineSession;
import com.example.codebasebackend.dto.AppointmentRequest;
import com.example.codebasebackend.dto.AppointmentResponse;
import com.example.codebasebackend.repositories.AppointmentRepository;
import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import com.example.codebasebackend.repositories.DoctorRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import com.example.codebasebackend.repositories.TelemedicineSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImplementation implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;
    private final CommunityHealthWorkersRepository communityHealthWorkersRepository;
    private final DoctorRepository doctorRepository;
    private final TelemedicineSessionRepository telemedicineSessionRepository;
    private final CommunityHealthWorkerAssignmentService assignmentService;

    @Override
    public AppointmentResponse create(AppointmentRequest request) {
        validateTimes(request.getScheduledStart(), request.getScheduledEnd());
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
        Hospital hospital = null;
        if (request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
        }
        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setHospital(hospital);
        appt.setScheduledStart(request.getScheduledStart());
        appt.setScheduledEnd(request.getScheduledEnd());
        appt.setProviderName(request.getProviderName());
        applyProvider(request, appt);
        appt.setRoom(request.getRoom());
        appt.setLocation(request.getLocation());
        appt.setReason(request.getReason());
        appt.setNotes(request.getNotes());
        appt.setReminderSent(request.getReminderSent());
        appt.setAppointmentCode(orGenerateCode(request.getAppointmentCode()));
        if (request.getStatus() != null) {
            appt.setStatus(parseStatus(request.getStatus()));
        } else {
            appt.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        }
        if (request.getType() != null) {
            appt.setType(parseType(request.getType()));
        }
        Appointment saved = appointmentRepository.save(appt);
        syncTelemedicineSession(saved);
        assignmentService.syncFromAppointment(saved);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse get(Long id) {
        return appointmentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Appointment not found"));
    }

    @Override
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        validateTimes(request.getScheduledStart(), request.getScheduledEnd());
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Appointment not found"));

        if (!appt.getPatient().getId().equals(request.getPatientId())) {
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
            appt.setPatient(patient);
        }
        if (request.getHospitalId() != null) {
            if (appt.getHospital() == null || !appt.getHospital().getId().equals(request.getHospitalId())) {
                Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
                appt.setHospital(hospital);
            }
        } else {
            appt.setHospital(null);
        }
        appt.setScheduledStart(request.getScheduledStart());
        appt.setScheduledEnd(request.getScheduledEnd());
        appt.setProviderName(request.getProviderName());
        applyProvider(request, appt);
        appt.setRoom(request.getRoom());
        appt.setLocation(request.getLocation());
        appt.setReason(request.getReason());
        appt.setNotes(request.getNotes());
        appt.setReminderSent(request.getReminderSent());
        appt.setAppointmentCode(orGenerateCode(request.getAppointmentCode()));
        if (request.getStatus() != null) appt.setStatus(parseStatus(request.getStatus()));
        if (request.getType() != null) appt.setType(parseType(request.getType()));

        Appointment saved = appointmentRepository.save(appt);
        syncTelemedicineSession(saved);
        assignmentService.syncFromAppointment(saved);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Appointment not found");
        }
        telemedicineSessionRepository.deleteByAppointmentId(id);
        assignmentService.removeForAppointment(id);
        appointmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> listByPatient(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByScheduledStartAsc(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> listByHospital(Long hospitalId) {
        return appointmentRepository.findByHospitalIdOrderByScheduledStartAsc(hospitalId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> listByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByScheduledStartAsc(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> listInRange(OffsetDateTime from, OffsetDateTime to) {
        if (from == null || to == null || !to.isAfter(from)) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid time range");
        }
        return appointmentRepository.findByScheduledStartBetweenOrderByScheduledStartAsc(from, to)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> listAll(String providerRole, Long doctorId, Long chwId) {
        Appointment.ProviderRole providerRoleEnum = null;
        if (providerRole != null && !providerRole.isBlank() && !providerRole.equalsIgnoreCase("all")) {
            providerRoleEnum = parseProviderRole(providerRole);
        }

        if (doctorId != null && chwId != null) {
            throw new ResponseStatusException(BAD_REQUEST, "Use either doctorId or chwId filter, not both");
        }

        if (providerRoleEnum == Appointment.ProviderRole.DOCTOR && chwId != null) {
            throw new ResponseStatusException(BAD_REQUEST, "chwId cannot be used with providerRole=DOCTOR");
        }

        if (providerRoleEnum == Appointment.ProviderRole.CHW && doctorId != null) {
            throw new ResponseStatusException(BAD_REQUEST, "doctorId cannot be used with providerRole=CHW");
        }

        if (providerRoleEnum == null && doctorId != null) {
            providerRoleEnum = Appointment.ProviderRole.DOCTOR;
        }

        if (providerRoleEnum == null && chwId != null) {
            providerRoleEnum = Appointment.ProviderRole.CHW;
        }

        return appointmentRepository.findAllWithProviderFilters(providerRoleEnum, doctorId, chwId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponse> searchAppointments(String status, String type, String searchTerm, int page, int size) {
        Appointment.AppointmentStatus statusEnum = null;
        if (status != null && !status.equalsIgnoreCase("all")) {
            statusEnum = parseStatus(status);
        }

        Appointment.AppointmentType typeEnum = null;
        if (type != null && !type.equalsIgnoreCase("all")) {
            typeEnum = parseType(type);
        }

        String search = (searchTerm != null && !searchTerm.isBlank()) ? searchTerm : null;

        Pageable pageable = PageRequest.of(page, size, Sort.by("scheduledStart").descending());

        Page<Appointment> appointments = appointmentRepository.searchAppointments(
            statusEnum, typeEnum, search, pageable
        );

        return appointments.map(this::toResponse);
    }

    @Override
    public AppointmentResponse checkIn(Long id) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Appointment not found"));

        if (appt.getCheckInTime() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "Patient already checked in");
        }

        appt.setCheckInTime(OffsetDateTime.now());
        appt.setStatus(Appointment.AppointmentStatus.CHECKED_IN);

        Appointment saved = appointmentRepository.save(appt);
        syncTelemedicineSession(saved);
        assignmentService.syncFromAppointment(saved);
        return toResponse(saved);
    }

    @Override
    public AppointmentResponse checkOut(Long id) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Appointment not found"));

        if (appt.getCheckInTime() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Patient has not checked in yet");
        }

        if (appt.getCheckOutTime() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "Patient already checked out");
        }

        appt.setCheckOutTime(OffsetDateTime.now());
        appt.setStatus(Appointment.AppointmentStatus.COMPLETED);

        Appointment saved = appointmentRepository.save(appt);
        syncTelemedicineSession(saved);
        assignmentService.syncFromAppointment(saved);
        return toResponse(saved);
    }

    @Override
    public AppointmentResponse cancel(Long id, String reason) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Appointment not found"));

        if (appt.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(BAD_REQUEST, "Cannot cancel completed appointment");
        }

        if (appt.getStatus() == Appointment.AppointmentStatus.CANCELED) {
            throw new ResponseStatusException(BAD_REQUEST, "Appointment already canceled");
        }

        appt.setStatus(Appointment.AppointmentStatus.CANCELED);

        // Append cancellation reason to notes
        String cancelNote = "CANCELED: " + (reason != null ? reason : "No reason provided");
        appt.setNotes(appt.getNotes() != null
            ? appt.getNotes() + "\n\n" + cancelNote
            : cancelNote);

        Appointment saved = appointmentRepository.save(appt);
        syncTelemedicineSession(saved);
        assignmentService.syncFromAppointment(saved);
        return toResponse(saved);
    }

    @Override
    public AppointmentResponse reschedule(Long id, OffsetDateTime newStart, OffsetDateTime newEnd) {
        validateTimes(newStart, newEnd);

        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Appointment not found"));

        if (appt.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(BAD_REQUEST, "Cannot reschedule completed appointment");
        }

        if (appt.getStatus() == Appointment.AppointmentStatus.CANCELED) {
            throw new ResponseStatusException(BAD_REQUEST, "Cannot reschedule canceled appointment");
        }

        // Store old times in notes for history
        String rescheduleNote = String.format("RESCHEDULED: Original time was %s to %s",
            appt.getScheduledStart(), appt.getScheduledEnd());
        appt.setNotes(appt.getNotes() != null
            ? appt.getNotes() + "\n\n" + rescheduleNote
            : rescheduleNote);

        appt.setScheduledStart(newStart);
        appt.setScheduledEnd(newEnd);
        appt.setStatus(Appointment.AppointmentStatus.RESCHEDULED);

        Appointment saved = appointmentRepository.save(appt);
        syncTelemedicineSession(saved);
        assignmentService.syncFromAppointment(saved);
        return toResponse(saved);
    }

    @Override
    public AppointmentResponse confirm(Long id) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Appointment not found"));

        if (appt.getStatus() != Appointment.AppointmentStatus.SCHEDULED) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Only scheduled appointments can be confirmed");
        }

        appt.setStatus(Appointment.AppointmentStatus.CONFIRMED);

        Appointment saved = appointmentRepository.save(appt);
        syncTelemedicineSession(saved);
        assignmentService.syncFromAppointment(saved);
        return toResponse(saved);
    }

    private void validateTimes(OffsetDateTime start, OffsetDateTime end) {
        if (start == null || end == null) {
            throw new ResponseStatusException(BAD_REQUEST, "scheduledStart and scheduledEnd are required");
        }
        if (!end.isAfter(start)) {
            throw new ResponseStatusException(BAD_REQUEST, "scheduledEnd must be after scheduledStart");
        }
    }

    private String orGenerateCode(String provided) {
        if (provided != null && !provided.isBlank()) return provided;
        return "APT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private Appointment.AppointmentStatus parseStatus(String s) {
        try { return Appointment.AppointmentStatus.valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid status"); }
    }

    private Appointment.AppointmentType parseType(String s) {
        try {
            String value = s.toUpperCase();
            if ("TELEMED".equals(value)) {
                value = "TELEMEDICINE";
            }
            return Appointment.AppointmentType.valueOf(value);
        }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid type"); }
    }

    private Appointment.ProviderRole parseProviderRole(String s) {
        try { return Appointment.ProviderRole.valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid providerRole"); }
    }

    private void applyProvider(AppointmentRequest request, Appointment appointment) {
        if (request.getDoctorId() != null && request.getChwId() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "Only one provider can be linked to an appointment");
        }

        String providerRoleText = request.getProviderRole();
        if ((providerRoleText == null || providerRoleText.isBlank()) && request.getDoctorId() != null) {
            providerRoleText = Appointment.ProviderRole.DOCTOR.name();
        } else if ((providerRoleText == null || providerRoleText.isBlank()) && request.getChwId() != null) {
            providerRoleText = Appointment.ProviderRole.CHW.name();
        }

        if (providerRoleText == null || providerRoleText.isBlank()) {
            appointment.setProviderRole(null);
            appointment.setChw(null);
            appointment.setDoctor(null);
            return;
        }

        Appointment.ProviderRole providerRole = parseProviderRole(providerRoleText);
        appointment.setProviderRole(providerRole);

        if (providerRole == Appointment.ProviderRole.CHW) {
            Long providerId = request.getChwId() != null ? request.getChwId() : request.getProviderId();
            if (providerId == null) {
                throw new ResponseStatusException(BAD_REQUEST, "providerId is required when providerRole is CHW");
            }
            CommunityHealthWorkers chw = communityHealthWorkersRepository.findById(providerId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));
            appointment.setChw(chw);
            appointment.setDoctor(null);
            if (request.getProviderName() == null || request.getProviderName().isBlank()) {
                appointment.setProviderName(buildChwName(chw));
            }
            return;
        }

        if (providerRole == Appointment.ProviderRole.DOCTOR) {
            Long providerId = request.getDoctorId() != null ? request.getDoctorId() : request.getProviderId();
            if (providerId == null) {
                throw new ResponseStatusException(BAD_REQUEST, "providerId is required when providerRole is DOCTOR");
            }
            Doctor doctor = doctorRepository.findById(providerId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Doctor not found"));
            appointment.setDoctor(doctor);
            appointment.setChw(null);
            if (request.getProviderName() == null || request.getProviderName().isBlank()) {
                appointment.setProviderName(doctor.getFullName());
            }
            return;
        }

        appointment.setChw(null);
        appointment.setDoctor(null);
    }

    private AppointmentResponse toResponse(Appointment a) {
        AppointmentResponse dto = new AppointmentResponse();
        dto.setId(a.getId());
        dto.setAppointmentCode(a.getAppointmentCode());

        // Patient details
        dto.setPatientId(a.getPatient() != null ? a.getPatient().getId() : null);
        if (a.getPatient() != null) {
            String fullName = buildFullName(a.getPatient().getFirstName(),
                                           a.getPatient().getMiddleName(),
                                           a.getPatient().getLastName());
            dto.setPatientName(fullName);
            dto.setPatientPhone(a.getPatient().getPhone());
            dto.setPatientEmail(a.getPatient().getEmail());
        }

        // Hospital details
        dto.setHospitalId(a.getHospital() != null ? a.getHospital().getId() : null);
        dto.setHospitalName(a.getHospital() != null ? a.getHospital().getName() : null);

        // Scheduling
        dto.setScheduledStart(a.getScheduledStart());
        dto.setScheduledEnd(a.getScheduledEnd());
        dto.setCheckInTime(a.getCheckInTime());
        dto.setCheckOutTime(a.getCheckOutTime());

        // Classification
        dto.setStatus(a.getStatus() != null ? a.getStatus().name() : null);
        dto.setType(a.getType() != null ? a.getType().name() : null);
        dto.setProviderRole(a.getProviderRole() != null ? a.getProviderRole().name() : null);
        if (a.getProviderRole() == Appointment.ProviderRole.CHW && a.getChw() != null) {
            dto.setProviderId(a.getChw().getId());
        } else if (a.getProviderRole() == Appointment.ProviderRole.DOCTOR && a.getDoctor() != null) {
            dto.setProviderId(a.getDoctor().getId());
        }
        dto.setDoctorId(a.getDoctor() != null ? a.getDoctor().getId() : null);
        dto.setChwId(a.getChw() != null ? a.getChw().getId() : null);
        dto.setTelemedicineSessionId(
                telemedicineSessionRepository.findByAppointmentId(a.getId()).map(TelemedicineSession::getId).orElse(null)
        );

        // Clinical/operational
        dto.setProviderName(a.getProviderName());
        dto.setRoom(a.getRoom());
        dto.setLocation(a.getLocation());
        dto.setReason(a.getReason());
        dto.setNotes(a.getNotes());
        dto.setReminderSent(a.getReminderSent());
        dto.setScheduledAt(a.getScheduledStart());

        // Audit fields
        dto.setCreatedAt(a.getCreatedAt());
        dto.setUpdatedAt(a.getUpdatedAt());

        return dto;
    }

    private String buildFullName(String firstName, String middleName, String lastName) {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            sb.append(firstName);
        }
        if (middleName != null && !middleName.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(middleName);
        }
        if (lastName != null && !lastName.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(lastName);
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private String buildChwName(CommunityHealthWorkers chw) {
        StringBuilder sb = new StringBuilder();
        if (chw.getFirstName() != null && !chw.getFirstName().isBlank()) {
            sb.append(chw.getFirstName());
        }
        if (chw.getMiddleName() != null && !chw.getMiddleName().isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(chw.getMiddleName());
        }
        if (chw.getLastName() != null && !chw.getLastName().isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(chw.getLastName());
        }
        return sb.toString();
    }

    private void syncTelemedicineSession(Appointment appointment) {
        if (appointment.getId() == null) {
            return;
        }

        boolean isTelemedicineType = appointment.getType() == Appointment.AppointmentType.TELEMEDICINE
                || appointment.getType() == Appointment.AppointmentType.TELEHEALTH;

        if (!isTelemedicineType) {
            telemedicineSessionRepository.deleteByAppointmentId(appointment.getId());
            return;
        }

        if (appointment.getProviderRole() != Appointment.ProviderRole.DOCTOR || appointment.getDoctor() == null) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Telemedicine appointments must be linked to a DOCTOR provider");
        }

        TelemedicineSession session = telemedicineSessionRepository
                .findByAppointmentId(appointment.getId())
                .orElseGet(TelemedicineSession::new);

        if (session.getSessionId() == null || session.getSessionId().isBlank()) {
            session.setSessionId(generateTelemedicineSessionId());
        }

        session.setAppointment(appointment);
        session.setPatient(appointment.getPatient());
        session.setDoctor(appointment.getDoctor());
        session.setHospital(appointment.getHospital());
        session.setSessionType(SessionType.CONSULTATION);
        session.setPlatform(PlatformType.VIDEO_CALL);
        session.setPriority(Priority.NORMAL);
        session.setStatus(mapAppointmentStatusToSessionStatus(appointment.getStatus()));
        session.setStartTime(appointment.getScheduledStart());
        session.setPlannedDuration(resolvePlannedDuration(appointment));
        if (session.getSymptoms() == null || session.getSymptoms().isEmpty()) {
            session.setSymptoms(Collections.singletonList("General consultation"));
        }
        if (session.getChiefComplaint() == null || session.getChiefComplaint().isBlank()) {
            session.setChiefComplaint(resolveChiefComplaint(appointment));
        }
        if (session.getCost() == null) {
            session.setCost(BigDecimal.ZERO);
        }
        if (session.getActualCost() == null) {
            session.setActualCost(session.getCost());
        }
        if (session.getPaymentStatus() == null || session.getPaymentStatus().isBlank()) {
            session.setPaymentStatus("PENDING");
        }
        if (session.getRecordingEnabled() == null) {
            session.setRecordingEnabled(false);
        }
        if (session.getReminderSent() == null) {
            session.setReminderSent(false);
        }
        if (session.getMeetingLink() == null || session.getMeetingLink().isBlank()) {
            session.setMeetingLink("https://telemedicine.medilink.com/session/" + session.getSessionId());
        }
        if (session.getMeetingId() == null || session.getMeetingId().isBlank()) {
            session.setMeetingId(UUID.randomUUID().toString());
        }

        telemedicineSessionRepository.save(session);
    }

    private SessionStatus mapAppointmentStatusToSessionStatus(Appointment.AppointmentStatus status) {
        if (status == null) {
            return SessionStatus.SCHEDULED;
        }
        return switch (status) {
            case CHECKED_IN, IN_PROGRESS -> SessionStatus.ACTIVE;
            case COMPLETED -> SessionStatus.COMPLETED;
            case CANCELED -> SessionStatus.CANCELLED;
            case NO_SHOW -> SessionStatus.NO_SHOW;
            default -> SessionStatus.SCHEDULED;
        };
    }

    private Integer resolvePlannedDuration(Appointment appointment) {
        if (appointment.getScheduledStart() == null || appointment.getScheduledEnd() == null) {
            return 30;
        }
        long minutes = ChronoUnit.MINUTES.between(appointment.getScheduledStart(), appointment.getScheduledEnd());
        if (minutes < 5) {
            return 5;
        }
        if (minutes > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) minutes;
    }

    private String resolveChiefComplaint(Appointment appointment) {
        if (appointment.getReason() != null && !appointment.getReason().isBlank()) {
            return appointment.getReason();
        }
        if (appointment.getNotes() != null && !appointment.getNotes().isBlank()) {
            return appointment.getNotes();
        }
        return "Telemedicine appointment";
    }

    private String generateTelemedicineSessionId() {
        return "TM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
