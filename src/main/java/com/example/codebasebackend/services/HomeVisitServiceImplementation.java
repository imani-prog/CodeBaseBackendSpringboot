package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.CommunityHealthWorkerAssignment;
import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.HomeVisit;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.dto.HomeVisitCancelRequest;
import com.example.codebasebackend.dto.HomeVisitCompleteRequest;
import com.example.codebasebackend.dto.HomeVisitLocationRequest;
import com.example.codebasebackend.dto.HomeVisitRequest;
import com.example.codebasebackend.dto.HomeVisitResponse;
import com.example.codebasebackend.dto.HomeVisitRescheduleRequest;
import com.example.codebasebackend.repositories.CommunityHealthWorkerAssignmentRepository;
import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import com.example.codebasebackend.repositories.HomeVisitRepository;
import com.example.codebasebackend.repositories.PatientRepository;
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
public class HomeVisitServiceImplementation implements HomeVisitService {

    private static final EnumSet<CommunityHealthWorkerAssignment.Status> ACTIVE_ASSIGNMENT_STATUSES = EnumSet.of(
            CommunityHealthWorkerAssignment.Status.ASSIGNED,
            CommunityHealthWorkerAssignment.Status.IN_PROGRESS
    );

    private final HomeVisitRepository homeVisitRepository;
    private final PatientRepository patientRepository;
    private final CommunityHealthWorkersRepository chwRepository;
    private final CommunityHealthWorkerAssignmentRepository assignmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HomeVisitResponse> list(Long patientId, Long chwId, HomeVisit.Status status) {
        return homeVisitRepository.findAll()
                .stream()
                .filter(v -> patientId == null || (v.getPatient() != null && patientId.equals(v.getPatient().getId())))
                .filter(v -> chwId == null || (v.getChw() != null && chwId.equals(v.getChw().getId())))
                .filter(v -> status == null || status == v.getStatus())
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HomeVisitResponse getById(Long id) {
        HomeVisit visit = homeVisitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Home visit not found"));
        return toResponse(visit);
    }

    @Override
    public HomeVisitResponse create(HomeVisitRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
        CommunityHealthWorkers chw = chwRepository.findById(request.getChwId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));

        HomeVisit visit = new HomeVisit();
        visit.setPatient(patient);
        visit.setChw(chw);
        applyRequest(visit, request);

        HomeVisit saved = homeVisitRepository.save(visit);
        ensureAssignmentForHomeVisit(saved);
        refreshAssignedPatientsCount(chw.getId());
        return toResponse(saved);
    }

    @Override
    public HomeVisitResponse update(Long id, HomeVisitRequest request) {
        HomeVisit visit = homeVisitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Home visit not found"));

        if (request.getPatientId() != null && (visit.getPatient() == null || !request.getPatientId().equals(visit.getPatient().getId()))) {
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
            visit.setPatient(patient);
        }

        Long previousChwId = visit.getChw() != null ? visit.getChw().getId() : null;
        if (request.getChwId() != null && (visit.getChw() == null || !request.getChwId().equals(visit.getChw().getId()))) {
            CommunityHealthWorkers chw = chwRepository.findById(request.getChwId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));
            visit.setChw(chw);
        }

        applyRequest(visit, request);
        HomeVisit saved = homeVisitRepository.save(visit);
        syncAssignmentFromHomeVisit(saved);

        if (previousChwId != null) refreshAssignedPatientsCount(previousChwId);
        if (saved.getChw() != null) refreshAssignedPatientsCount(saved.getChw().getId());

        return toResponse(saved);
    }

    @Override
    public HomeVisitResponse complete(Long id, HomeVisitCompleteRequest request) {
        HomeVisit visit = homeVisitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Home visit not found"));

        visit.setStatus(HomeVisit.Status.COMPLETED);
        visit.setCompletedAt(OffsetDateTime.now());
        visit.setOutcome(request.getOutcome());
        if (request.getNotes() != null) visit.setNotes(request.getNotes());

        HomeVisit saved = homeVisitRepository.save(visit);
        syncAssignmentFromHomeVisit(saved);
        if (saved.getChw() != null) refreshAssignedPatientsCount(saved.getChw().getId());
        return toResponse(saved);
    }

    @Override
    public HomeVisitResponse cancel(Long id, HomeVisitCancelRequest request) {
        HomeVisit visit = homeVisitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Home visit not found"));

        visit.setStatus(HomeVisit.Status.CANCELED);
        visit.setCanceledAt(OffsetDateTime.now());
        if (request != null && request.getReason() != null && !request.getReason().isBlank()) {
            visit.setReason(request.getReason());
        }

        HomeVisit saved = homeVisitRepository.save(visit);
        syncAssignmentFromHomeVisit(saved);
        if (saved.getChw() != null) refreshAssignedPatientsCount(saved.getChw().getId());
        return toResponse(saved);
    }

    @Override
    public HomeVisitResponse reschedule(Long id, HomeVisitRescheduleRequest request) {
        if (request == null || request.getScheduledAt() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "scheduledAt is required");
        }

        HomeVisit visit = homeVisitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Home visit not found"));

        visit.setScheduledAt(request.getScheduledAt());
        if (request.getReason() != null && !request.getReason().isBlank()) {
            visit.setReason(request.getReason());
        }
        if (visit.getStatus() == HomeVisit.Status.CANCELED || visit.getStatus() == HomeVisit.Status.NO_SHOW) {
            visit.setStatus(HomeVisit.Status.SCHEDULED);
            visit.setCanceledAt(null);
        }

        HomeVisit saved = homeVisitRepository.save(visit);
        syncAssignmentFromHomeVisit(saved);
        if (saved.getChw() != null) refreshAssignedPatientsCount(saved.getChw().getId());
        return toResponse(saved);
    }

    @Override
    public HomeVisitResponse updateLocation(Long id, HomeVisitLocationRequest request) {
        HomeVisit visit = homeVisitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Home visit not found"));

        visit.setLocation(request != null ? request.getLocation() : null);
        visit.setLatitude(request != null ? request.getLatitude() : null);
        visit.setLongitude(request != null ? request.getLongitude() : null);

        HomeVisit saved = homeVisitRepository.save(visit);
        syncAssignmentFromHomeVisit(saved);
        return toResponse(saved);
    }

    private void applyRequest(HomeVisit visit, HomeVisitRequest request) {
        if (request.getVisitType() != null) visit.setVisitType(request.getVisitType());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            visit.setStatus(parseHomeVisitStatus(request.getStatus()));
        }
        if (request.getPriority() != null && !request.getPriority().isBlank()) {
            visit.setPriority(parsePriority(request.getPriority()));
        }
        if (request.getScheduledAt() != null) visit.setScheduledAt(request.getScheduledAt());
        visit.setLocation(request.getLocation());
        visit.setLatitude(request.getLatitude());
        visit.setLongitude(request.getLongitude());
        visit.setReason(request.getReason());
        visit.setNotes(request.getNotes());
    }

    private void ensureAssignmentForHomeVisit(HomeVisit visit) {
        CommunityHealthWorkerAssignment assignment = assignmentRepository.findByHomeVisitId(visit.getId())
                .orElseGet(CommunityHealthWorkerAssignment::new);

        assignment.setHomeVisit(visit);
        assignment.setPatient(visit.getPatient());
        assignment.setChw(visit.getChw());
        assignment.setAssignmentType(CommunityHealthWorkerAssignment.AssignmentType.HOME_VISIT);
        assignment.setStatus(mapAssignmentStatus(visit.getStatus()));
        assignment.setLocation(visit.getLocation());
        assignment.setNotes(visit.getNotes());

        assignmentRepository.save(assignment);
    }

    private void syncAssignmentFromHomeVisit(HomeVisit visit) {
        CommunityHealthWorkerAssignment assignment = assignmentRepository.findByHomeVisitId(visit.getId())
                .orElseGet(CommunityHealthWorkerAssignment::new);

        assignment.setHomeVisit(visit);
        assignment.setPatient(visit.getPatient());
        assignment.setChw(visit.getChw());
        assignment.setAssignmentType(CommunityHealthWorkerAssignment.AssignmentType.HOME_VISIT);
        assignment.setStatus(mapAssignmentStatus(visit.getStatus()));
        assignment.setLocation(visit.getLocation());
        assignment.setNotes(visit.getNotes());

        assignmentRepository.save(assignment);
    }

    private HomeVisit.Status parseHomeVisitStatus(String raw) {
        try {
            return HomeVisit.Status.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid home visit status");
        }
    }

    private HomeVisit.Priority parsePriority(String raw) {
        try {
            return HomeVisit.Priority.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid priority");
        }
    }

    private CommunityHealthWorkerAssignment.Status mapAssignmentStatus(HomeVisit.Status status) {
        if (status == null) return CommunityHealthWorkerAssignment.Status.ASSIGNED;
        return switch (status) {
            case IN_PROGRESS -> CommunityHealthWorkerAssignment.Status.IN_PROGRESS;
            case COMPLETED -> CommunityHealthWorkerAssignment.Status.COMPLETED;
            case CANCELED, NO_SHOW -> CommunityHealthWorkerAssignment.Status.CANCELED;
            default -> CommunityHealthWorkerAssignment.Status.ASSIGNED;
        };
    }

    private void refreshAssignedPatientsCount(Long chwId) {
        if (chwId == null) return;
        CommunityHealthWorkers chw = chwRepository.findById(chwId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));
        long activeCount = assignmentRepository.countByChwIdAndStatusIn(chwId, ACTIVE_ASSIGNMENT_STATUSES);
        chw.setAssignedPatients(Math.toIntExact(activeCount));
        chwRepository.save(chw);
    }

    private HomeVisitResponse toResponse(HomeVisit visit) {
        HomeVisitResponse response = new HomeVisitResponse();
        response.setId(visit.getId());
        response.setPatientId(visit.getPatient() != null ? visit.getPatient().getId() : null);
        response.setPatientName(buildPatientName(visit.getPatient()));
        response.setChwId(visit.getChw() != null ? visit.getChw().getId() : null);
        response.setChwName(buildChwName(visit.getChw()));
        response.setVisitType(visit.getVisitType());
        response.setStatus(visit.getStatus() != null ? visit.getStatus().name() : null);
        response.setPriority(visit.getPriority() != null ? visit.getPriority().name() : null);
        response.setScheduledAt(visit.getScheduledAt());
        response.setCompletedAt(visit.getCompletedAt());
        response.setCanceledAt(visit.getCanceledAt());
        response.setLocation(visit.getLocation());
        response.setLatitude(visit.getLatitude());
        response.setLongitude(visit.getLongitude());
        response.setReason(visit.getReason());
        response.setNotes(visit.getNotes());
        response.setOutcome(visit.getOutcome());
        response.setAssignmentId(assignmentRepository.findByHomeVisitId(visit.getId()).map(CommunityHealthWorkerAssignment::getId).orElse(null));
        response.setCreatedAt(visit.getCreatedAt());
        response.setUpdatedAt(visit.getUpdatedAt());
        return response;
    }

    private String buildPatientName(Patient patient) {
        if (patient == null) return null;
        StringBuilder sb = new StringBuilder();
        if (patient.getFirstName() != null && !patient.getFirstName().isBlank()) sb.append(patient.getFirstName());
        if (patient.getMiddleName() != null && !patient.getMiddleName().isBlank()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(patient.getMiddleName());
        }
        if (patient.getLastName() != null && !patient.getLastName().isBlank()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(patient.getLastName());
        }
        return sb.toString();
    }

    private String buildChwName(CommunityHealthWorkers chw) {
        if (chw == null) return null;
        StringBuilder sb = new StringBuilder();
        if (chw.getFirstName() != null && !chw.getFirstName().isBlank()) sb.append(chw.getFirstName());
        if (chw.getMiddleName() != null && !chw.getMiddleName().isBlank()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(chw.getMiddleName());
        }
        if (chw.getLastName() != null && !chw.getLastName().isBlank()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(chw.getLastName());
        }
        return sb.toString();
    }
}

