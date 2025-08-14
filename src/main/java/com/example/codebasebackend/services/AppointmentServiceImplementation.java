package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Appointment;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.dto.AppointmentRequest;
import com.example.codebasebackend.dto.AppointmentResponse;
import com.example.codebasebackend.repositories.AppointmentRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
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
        appt.setRoom(request.getRoom());
        appt.setLocation(request.getLocation());
        appt.setReason(request.getReason());
        appt.setNotes(request.getNotes());
        appt.setReminderSent(request.getReminderSent());
        appt.setAppointmentCode(orGenerateCode(request.getAppointmentCode()));
        if (request.getStatus() != null) appt.setStatus(parseStatus(request.getStatus()));
        if (request.getType() != null) appt.setType(parseType(request.getType()));

        Appointment saved = appointmentRepository.save(appt);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Appointment not found");
        }
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
        try { return Appointment.AppointmentType.valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid type"); }
    }

    private AppointmentResponse toResponse(Appointment a) {
        AppointmentResponse dto = new AppointmentResponse();
        dto.setId(a.getId());
        dto.setAppointmentCode(a.getAppointmentCode());
        dto.setPatientId(a.getPatient() != null ? a.getPatient().getId() : null);
        dto.setHospitalId(a.getHospital() != null ? a.getHospital().getId() : null);
        dto.setScheduledStart(a.getScheduledStart());
        dto.setScheduledEnd(a.getScheduledEnd());
        dto.setCheckInTime(a.getCheckInTime());
        dto.setCheckOutTime(a.getCheckOutTime());
        dto.setStatus(a.getStatus() != null ? a.getStatus().name() : null);
        dto.setType(a.getType() != null ? a.getType().name() : null);
        dto.setProviderName(a.getProviderName());
        dto.setRoom(a.getRoom());
        dto.setLocation(a.getLocation());
        dto.setReason(a.getReason());
        dto.setNotes(a.getNotes());
        dto.setReminderSent(a.getReminderSent());
        return dto;
    }
}
