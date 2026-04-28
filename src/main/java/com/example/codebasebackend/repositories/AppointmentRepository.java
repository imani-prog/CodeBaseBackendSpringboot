package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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
    long countByStatusIn(List<Appointment.AppointmentStatus> statuses);
    long countByStatus(Appointment.AppointmentStatus status);
    long countByScheduledStartGreaterThanEqualAndScheduledStartLessThan(OffsetDateTime from, OffsetDateTime to);
    long countByProviderRoleAndTypeInAndScheduledStartGreaterThanEqualAndScheduledStartLessThan(
        Appointment.ProviderRole providerRole,
        List<Appointment.AppointmentType> types,
        OffsetDateTime from,
        OffsetDateTime to
    );

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.scheduledStart >= :from AND a.scheduledStart < :to " +
           "AND a.checkInTime IS NOT NULL AND a.checkInTime <= a.scheduledStart")
    long countOnTimeCheckIns(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);

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

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:providerRole IS NULL OR a.providerRole = :providerRole) AND " +
           "(:doctorId IS NULL OR a.doctor.id = :doctorId) AND " +
           "(:chwId IS NULL OR a.chw.id = :chwId) " +
           "ORDER BY a.scheduledStart DESC")
    List<Appointment> findAllWithProviderFilters(
        @Param("providerRole") Appointment.ProviderRole providerRole,
        @Param("doctorId") Long doctorId,
        @Param("chwId") Long chwId
    );

    Optional<Appointment> findFirstByPatientIdAndDoctorIdAndScheduledStartAndType(
        Long patientId,
        Long doctorId,
        OffsetDateTime scheduledStart,
        Appointment.AppointmentType type
    );
}
