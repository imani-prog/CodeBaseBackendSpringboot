package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.AppointmentRequest;
import com.example.codebasebackend.dto.AppointmentResponse;
import com.example.codebasebackend.Entities.Appointment;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse create(AppointmentRequest request);
    AppointmentResponse get(Long id);
    AppointmentResponse update(Long id, AppointmentRequest request);
    void delete(Long id);

    List<AppointmentResponse> listByPatient(Long patientId);
    List<AppointmentResponse> listByHospital(Long hospitalId);
    List<AppointmentResponse> listByStatus(Appointment.AppointmentStatus status);
    List<AppointmentResponse> listInRange(OffsetDateTime from, OffsetDateTime to);
    List<AppointmentResponse> listAll();

    // New methods
    Page<AppointmentResponse> searchAppointments(String status, String type, String searchTerm, int page, int size);
    AppointmentResponse checkIn(Long id);
    AppointmentResponse checkOut(Long id);
    AppointmentResponse cancel(Long id, String reason);
    AppointmentResponse reschedule(Long id, OffsetDateTime newStart, OffsetDateTime newEnd);
    AppointmentResponse confirm(Long id);
}
