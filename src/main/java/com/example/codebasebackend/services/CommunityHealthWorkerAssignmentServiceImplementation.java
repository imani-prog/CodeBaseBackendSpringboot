package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Appointment;
import com.example.codebasebackend.Entities.CommunityHealthWorkerAssignment;
import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.dto.ChwAssignmentReassignRequest;
import com.example.codebasebackend.dto.ChwAssignmentRequest;
import com.example.codebasebackend.dto.ChwAssignmentResponse;
import com.example.codebasebackend.repositories.AppointmentRepository;
import com.example.codebasebackend.repositories.CommunityHealthWorkerAssignmentRepository;
import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityHealthWorkerAssignmentServiceImplementation implements CommunityHealthWorkerAssignmentService {

    private static final EnumSet<CommunityHealthWorkerAssignment.Status> ACTIVE_STATUSES = EnumSet.of(
            CommunityHealthWorkerAssignment.Status.ASSIGNED,
            CommunityHealthWorkerAssignment.Status.IN_PROGRESS
    );

    private final CommunityHealthWorkerAssignmentRepository assignmentRepository;
    private final PatientRepository patientRepository;
    private final CommunityHealthWorkersRepository chwRepository;
    private final AppointmentRepository appointmentRepository;

    @PostConstruct
    void reconcileAssignedPatientsOnStartup() {
        for (CommunityHealthWorkers chw : chwRepository.findAll()) {
            refreshAssignedPatientsCount(chw.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChwAssignmentResponse> list(Long patientId,
                                            Long chwId,
                                            CommunityHealthWorkerAssignment.Status status,
                                            CommunityHealthWorkerAssignment.AssignmentType assignmentType) {
        return assignmentRepository.findAll()
                .stream()
                .filter(a -> patientId == null || (a.getPatient() != null && patientId.equals(a.getPatient().getId())))
                .filter(a -> chwId == null || (a.getChw() != null && chwId.equals(a.getChw().getId())))
                .filter(a -> status == null || status == a.getStatus())
                .filter(a -> assignmentType == null || assignmentType == a.getAssignmentType())
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChwAssignmentResponse getById(Long id) {
        CommunityHealthWorkerAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));
        return toResponse(assignment);
    }

    @Override
    public ChwAssignmentResponse create(ChwAssignmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));

        CommunityHealthWorkers chw = chwRepository.findById(request.getChwId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));

        CommunityHealthWorkerAssignment.AssignmentType assignmentType = parseAssignmentType(request.getAssignmentType());
        CommunityHealthWorkerAssignment.Status status = parseStatus(request.getStatus());

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Appointment not found"));
        }

        if (assignmentType == CommunityHealthWorkerAssignment.AssignmentType.APPOINTMENT && appointment == null) {
            throw new ResponseStatusException(BAD_REQUEST, "appointmentId is required for APPOINTMENT assignments");
        }

        CommunityHealthWorkerAssignment assignment = new CommunityHealthWorkerAssignment();
        assignment.setPatient(patient);
        assignment.setChw(chw);
        assignment.setAssignmentType(assignmentType);
        assignment.setStatus(status);
        assignment.setAppointment(appointment);
        assignment.setLocation(request.getLocation());
        assignment.setNotes(request.getNotes());

        if (status == CommunityHealthWorkerAssignment.Status.IN_PROGRESS) {
            assignment.setStartedAt(OffsetDateTime.now());
        }
        if (status == CommunityHealthWorkerAssignment.Status.COMPLETED) {
            assignment.setStartedAt(OffsetDateTime.now());
            assignment.setCompletedAt(OffsetDateTime.now());
        }

        CommunityHealthWorkerAssignment saved = assignmentRepository.save(assignment);
        refreshAssignedPatientsCount(chw.getId());
        return toResponse(saved);
    }

    @Override
    public ChwAssignmentResponse updateStatus(Long id, CommunityHealthWorkerAssignment.Status status) {
        CommunityHealthWorkerAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));

        assignment.setStatus(status);
        if (status == CommunityHealthWorkerAssignment.Status.IN_PROGRESS && assignment.getStartedAt() == null) {
            assignment.setStartedAt(OffsetDateTime.now());
        }
        if ((status == CommunityHealthWorkerAssignment.Status.COMPLETED
                || status == CommunityHealthWorkerAssignment.Status.CANCELED)
                && assignment.getCompletedAt() == null) {
            assignment.setCompletedAt(OffsetDateTime.now());
        }

        CommunityHealthWorkerAssignment saved = assignmentRepository.save(assignment);
        if (saved.getChw() != null) {
            refreshAssignedPatientsCount(saved.getChw().getId());
        }
        return toResponse(saved);
    }

    @Override
    public ChwAssignmentResponse reassign(Long id, ChwAssignmentReassignRequest request) {
        CommunityHealthWorkerAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));
        Long previousChwId = assignment.getChw() != null ? assignment.getChw().getId() : null;

        CommunityHealthWorkers targetChw = chwRepository.findById(request.getChwId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));

        assignment.setChw(targetChw);
        if (request.getReason() != null && !request.getReason().isBlank()) {
            String existing = assignment.getNotes();
            String note = "REASSIGNED: " + request.getReason();
            assignment.setNotes(existing == null || existing.isBlank() ? note : existing + "\n" + note);
        }

        CommunityHealthWorkerAssignment saved = assignmentRepository.save(assignment);
        if (previousChwId != null) {
            refreshAssignedPatientsCount(previousChwId);
        }
        refreshAssignedPatientsCount(targetChw.getId());
        return toResponse(saved);
    }

    @Override
    public ChwAssignmentResponse replace(Long id, ChwAssignmentRequest request) {
        CommunityHealthWorkerAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));
        Long previousChwId = assignment.getChw() != null ? assignment.getChw().getId() : null;

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));

        CommunityHealthWorkers chw = chwRepository.findById(request.getChwId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));

        assignment.setPatient(patient);
        assignment.setChw(chw);
        assignment.setAssignmentType(parseAssignmentType(request.getAssignmentType()));
        assignment.setStatus(parseStatus(request.getStatus()));
        assignment.setLocation(request.getLocation());
        assignment.setNotes(request.getNotes());

        if (request.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Appointment not found"));
            assignment.setAppointment(appointment);
        } else {
            assignment.setAppointment(null);
        }

        CommunityHealthWorkerAssignment saved = assignmentRepository.save(assignment);
        if (previousChwId != null) {
            refreshAssignedPatientsCount(previousChwId);
        }
        refreshAssignedPatientsCount(chw.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChwAssignmentResponse> listByPatient(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
        return assignmentRepository.findByPatientIdOrderByAssignedAtDesc(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChwAssignmentResponse> listByChw(Long chwId) {
        chwRepository.findById(chwId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));
        return assignmentRepository.findByChwIdOrderByAssignedAtDesc(chwId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void syncFromAppointment(Appointment appointment) {
        if (appointment == null) return;

        if (appointment.getProviderRole() != Appointment.ProviderRole.CHW || appointment.getChw() == null) {
            removeForAppointment(appointment.getId());
            return;
        }

        CommunityHealthWorkerAssignment assignment = assignmentRepository.findByAppointmentId(appointment.getId())
                .orElseGet(CommunityHealthWorkerAssignment::new);

        assignment.setAppointment(appointment);
        assignment.setPatient(appointment.getPatient());
        assignment.setChw(appointment.getChw());
        assignment.setAssignmentType(CommunityHealthWorkerAssignment.AssignmentType.APPOINTMENT);
        assignment.setStatus(mapStatusFromAppointment(appointment.getStatus()));
        assignment.setLocation(appointment.getLocation());

        if (assignment.getStatus() == CommunityHealthWorkerAssignment.Status.IN_PROGRESS
                && assignment.getStartedAt() == null) {
            assignment.setStartedAt(OffsetDateTime.now());
        }
        if ((assignment.getStatus() == CommunityHealthWorkerAssignment.Status.COMPLETED
                || assignment.getStatus() == CommunityHealthWorkerAssignment.Status.CANCELED)
                && assignment.getCompletedAt() == null) {
            assignment.setCompletedAt(OffsetDateTime.now());
        }

        CommunityHealthWorkerAssignment saved = assignmentRepository.save(assignment);
        if (saved.getChw() != null) {
            refreshAssignedPatientsCount(saved.getChw().getId());
        }
    }

    @Override
    public void removeForAppointment(Long appointmentId) {
        if (appointmentId == null) return;
        Long chwId = assignmentRepository.findByAppointmentId(appointmentId)
                .map(existing -> existing.getChw() != null ? existing.getChw().getId() : null)
                .orElse(null);
        assignmentRepository.deleteByAppointmentId(appointmentId);
        if (chwId != null) {
            refreshAssignedPatientsCount(chwId);
        }
    }

    private void refreshAssignedPatientsCount(Long chwId) {
        if (chwId == null) return;
        CommunityHealthWorkers chw = chwRepository.findById(chwId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));
        long activeCount = assignmentRepository.countByChwIdAndStatusIn(chwId, ACTIVE_STATUSES);
        chw.setAssignedPatients(Math.toIntExact(activeCount));
        chwRepository.save(chw);
    }

    private CommunityHealthWorkerAssignment.Status mapStatusFromAppointment(Appointment.AppointmentStatus status) {
        if (status == null) return CommunityHealthWorkerAssignment.Status.ASSIGNED;
        return switch (status) {
            case CHECKED_IN, IN_PROGRESS, CONFIRMED -> CommunityHealthWorkerAssignment.Status.IN_PROGRESS;
            case COMPLETED -> CommunityHealthWorkerAssignment.Status.COMPLETED;
            case CANCELED, NO_SHOW -> CommunityHealthWorkerAssignment.Status.CANCELED;
            default -> CommunityHealthWorkerAssignment.Status.ASSIGNED;
        };
    }

    private CommunityHealthWorkerAssignment.AssignmentType parseAssignmentType(String value) {
        if (value == null || value.isBlank()) return CommunityHealthWorkerAssignment.AssignmentType.TASK;
        try {
            return CommunityHealthWorkerAssignment.AssignmentType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid assignmentType");
        }
    }

    private CommunityHealthWorkerAssignment.Status parseStatus(String value) {
        if (value == null || value.isBlank()) return CommunityHealthWorkerAssignment.Status.ASSIGNED;
        try {
            return CommunityHealthWorkerAssignment.Status.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid status");
        }
    }

    private ChwAssignmentResponse toResponse(CommunityHealthWorkerAssignment assignment) {
        ChwAssignmentResponse response = new ChwAssignmentResponse();
        response.setId(assignment.getId());

        if (assignment.getPatient() != null) {
            response.setPatientId(assignment.getPatient().getId());
            response.setPatientName(buildPatientName(assignment.getPatient()));
        }

        if (assignment.getChw() != null) {
            response.setChwId(assignment.getChw().getId());
            response.setChwName(buildChwName(assignment.getChw()));
        }

        response.setAssignmentType(assignment.getAssignmentType() != null ? assignment.getAssignmentType().name() : null);
        response.setSourceType(response.getAssignmentType());
        response.setStatus(assignment.getStatus() != null ? assignment.getStatus().name() : null);
        response.setAssignedAt(assignment.getAssignedAt());
        response.setStartedAt(assignment.getStartedAt());
        response.setCompletedAt(assignment.getCompletedAt());
        response.setAppointmentId(assignment.getAppointment() != null ? assignment.getAppointment().getId() : null);
        response.setLocation(assignment.getLocation());
        response.setNotes(assignment.getNotes());
        response.setCreatedAt(assignment.getCreatedAt());
        response.setUpdatedAt(assignment.getUpdatedAt());
        return response;
    }

    private String buildPatientName(Patient patient) {
        StringBuilder sb = new StringBuilder();
        if (patient.getFirstName() != null && !patient.getFirstName().isBlank()) sb.append(patient.getFirstName());
        if (patient.getMiddleName() != null && !patient.getMiddleName().isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(patient.getMiddleName());
        }
        if (patient.getLastName() != null && !patient.getLastName().isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(patient.getLastName());
        }
        return sb.toString();
    }

    private String buildChwName(CommunityHealthWorkers chw) {
        StringBuilder sb = new StringBuilder();
        if (chw.getFirstName() != null && !chw.getFirstName().isBlank()) sb.append(chw.getFirstName());
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
}


