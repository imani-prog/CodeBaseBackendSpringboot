package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientIdOrderByScheduledStartAsc(Long patientId);
    List<Appointment> findByHospitalIdOrderByScheduledStartAsc(Long hospitalId);
    List<Appointment> findByStatusOrderByScheduledStartAsc(Appointment.AppointmentStatus status);
    List<Appointment> findByScheduledStartBetweenOrderByScheduledStartAsc(OffsetDateTime from, OffsetDateTime to);
}
