package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Existing methods
    List<Appointment> findByPatientIdOrderByScheduledStartAsc(Long patientId);
    List<Appointment> findByHospitalIdOrderByScheduledStartAsc(Long hospitalId);
    List<Appointment> findByStatusOrderByScheduledStartAsc(Appointment.AppointmentStatus status);
    List<Appointment> findByScheduledStartBetweenOrderByScheduledStartAsc(OffsetDateTime from, OffsetDateTime to);

    // Paginated methods
    Page<Appointment> findByStatus(Appointment.AppointmentStatus status, Pageable pageable);
    Page<Appointment> findByType(Appointment.AppointmentType type, Pageable pageable);
    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);
    Page<Appointment> findByHospitalId(Long hospitalId, Pageable pageable);

    // Combined search query
    @Query("SELECT a FROM Appointment a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:type IS NULL OR a.type = :type) AND " +
           "(:searchTerm IS NULL OR " +
           "  LOWER(a.appointmentCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "  LOWER(a.patient.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "  LOWER(a.patient.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "  LOWER(a.providerName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY a.scheduledStart DESC")
    Page<Appointment> searchAppointments(
        @Param("status") Appointment.AppointmentStatus status,
        @Param("type") Appointment.AppointmentType type,
        @Param("searchTerm") String searchTerm,
        Pageable pageable
    );
}
