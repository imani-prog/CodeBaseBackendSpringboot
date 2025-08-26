package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.AmbulanceDispatch;
import com.example.codebasebackend.Entities.CommunityHealthWorkerAssignment;
import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.AssistanceResponse;
import com.example.codebasebackend.repositories.AmbulanceDispatchRepository;
import com.example.codebasebackend.repositories.CommunityHealthWorkerAssignmentRepository;
import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class AssistanceServiceImplementation implements AssistanceService {

    private final HospitalRepository hospitalRepo;
    private final PatientRepository patientRepo;
    private final AmbulanceDispatchRepository dispatchRepo;
    private final CommunityHealthWorkersService chwService;
    private final CommunityHealthWorkersRepository chwRepo;
    private final CommunityHealthWorkerAssignmentRepository assignmentRepo;

    @Override
    public AssistanceResponse requestAssistance(AssistanceRequest r) {
        if (r.getPickupLatitude() == null || r.getPickupLongitude() == null)
            throw new ResponseStatusException(BAD_REQUEST, "pickup lat/lon required");

        Patient patient = null;
        if (r.getPatientId() != null) {
            patient = patientRepo.findById(r.getPatientId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
        }

        Hospital hospital = null;
        if (r.getHospitalId() != null) {
            hospital = hospitalRepo.findById(r.getHospitalId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
        }

        boolean ambulanceAvailable = false;
        if (hospital != null && hospital.getNumberOfAmbulances() != null && hospital.getNumberOfAmbulances() > 0) {
            var activeStatuses = List.of(
                    AmbulanceDispatch.DispatchStatus.DISPATCHED,
                    AmbulanceDispatch.DispatchStatus.EN_ROUTE,
                    AmbulanceDispatch.DispatchStatus.ON_SCENE,
                    AmbulanceDispatch.DispatchStatus.TRANSPORTING
            );
            long active = dispatchRepo.countByHospitalIdAndStatusIn(hospital.getId(), activeStatuses);
            ambulanceAvailable = active < hospital.getNumberOfAmbulances();
        }

        if (ambulanceAvailable) {
            AmbulanceDispatch dispatch = new AmbulanceDispatch();
            dispatch.setIncidentId(UUID.randomUUID().toString());
            dispatch.setIncidentType(r.getIncidentType());
            dispatch.setCallerName(r.getCallerName());
            dispatch.setCallerPhone(r.getCallerPhone());
            dispatch.setCallerNotes(r.getNotes());
            dispatch.setPickupLatitude(r.getPickupLatitude());
            dispatch.setPickupLongitude(r.getPickupLongitude());
            dispatch.setPickupAddressLine1(r.getPickupAddressLine1());
            dispatch.setPickupAddressLine2(r.getPickupAddressLine2());
            dispatch.setPickupCity(r.getPickupCity());
            dispatch.setPickupState(r.getPickupState());
            dispatch.setPickupPostalCode(r.getPickupPostalCode());
            dispatch.setPickupCountry(r.getPickupCountry());
            dispatch.setHospital(hospital);
            dispatch.setPatient(patient);
            dispatch.setRequestTime(OffsetDateTime.now());
            dispatch.setStatus(AmbulanceDispatch.DispatchStatus.REQUESTED);
            AmbulanceDispatch.DispatchPriority priority = parsePriority(r.getPriority());
            dispatch.setPriority(priority);
            AmbulanceDispatch saved = dispatchRepo.save(dispatch);

            AssistanceResponse response = new AssistanceResponse();
            response.setMode(AssistanceResponse.Mode.AMBULANCE);
            response.setIncidentId(saved.getIncidentId());
            response.setPatientId(patient != null ? patient.getId() : null);
            response.setHospitalId(hospital != null ? hospital.getId() : null);
            response.setPriority(priority != null ? priority.name() : null);
            response.setStatus(saved.getStatus().name());
            response.setCreatedAt(saved.getCreatedAt());
            return response;
        }

        // Fallback to nearest available CHW
        var chwDto = chwService.findNearestAvailable(r.getPickupLatitude(), r.getPickupLongitude(), r.getHospitalId());
        CommunityHealthWorkers chw = chwRepo.findById(chwDto.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));

        CommunityHealthWorkerAssignment assign = new CommunityHealthWorkerAssignment();
        assign.setChw(chw);
        assign.setPatient(patient);
        assign.setStatus(CommunityHealthWorkerAssignment.Status.ASSIGNED);
        assign.setAssignedAt(OffsetDateTime.now());
        CommunityHealthWorkerAssignment savedAssign = assignmentRepo.save(assign);

        // mark CHW busy
        chw.setStatus(CommunityHealthWorkers.Status.BUSY);
        chwRepo.save(chw);

        AssistanceResponse response = new AssistanceResponse();
        response.setMode(AssistanceResponse.Mode.CHW);
        response.setAssignmentId(savedAssign.getId());
        response.setPatientId(patient != null ? patient.getId() : null);
        response.setHospitalId(hospital != null ? hospital.getId() : null);
        response.setChwId(chw.getId());
        response.setPriority(r.getPriority());
        response.setStatus(savedAssign.getStatus().name());
        response.setCreatedAt(savedAssign.getCreatedAt());
        return response;
    }

    @Override
    public List<AssistanceResponse> getAllDispatches() {
        return dispatchRepo.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AssistanceResponse getDispatchById(Long id) {
        AmbulanceDispatch dispatch = dispatchRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Dispatch not found"));
        return mapToResponse(dispatch);
    }

    @Override
    public AssistanceResponse updateDispatch(Long id, AssistanceRequest request) {
        AmbulanceDispatch dispatch = dispatchRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Dispatch not found"));

        // Update fields based on the request
        dispatch.setDropoffAddressLine1(request.getDropoffAddressLine1());
        dispatch.setDropoffCity(request.getDropoffCity());
        dispatch.setDropoffLatitude(request.getDropoffLatitude());
        dispatch.setDropoffLongitude(request.getDropoffLongitude());
        // Add other fields as necessary

        dispatchRepo.save(dispatch);
        return mapToResponse(dispatch);
    }

    @Override
    public void deleteDispatch(Long id) {
        if (!dispatchRepo.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Dispatch not found");
        }
        dispatchRepo.deleteById(id);
    }

    private AssistanceResponse mapToResponse(AmbulanceDispatch dispatch) {
        AssistanceResponse response = new AssistanceResponse();
        response.setIncidentId(dispatch.getIncidentId());
        response.setPatientId(dispatch.getPatient() != null ? dispatch.getPatient().getId() : null);
        response.setHospitalId(dispatch.getHospital() != null ? dispatch.getHospital().getId() : null);
        response.setPriority(dispatch.getPriority() != null ? dispatch.getPriority().name() : null);
        response.setStatus(dispatch.getStatus().name());
        response.setCreatedAt(dispatch.getRequestTime());
        return response;
    }

    private AmbulanceDispatch.DispatchPriority parsePriority(String s) {
        if (s == null) return AmbulanceDispatch.DispatchPriority.MEDIUM;
        try { return AmbulanceDispatch.DispatchPriority.valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException ex) { return AmbulanceDispatch.DispatchPriority.MEDIUM; }
    }
}
