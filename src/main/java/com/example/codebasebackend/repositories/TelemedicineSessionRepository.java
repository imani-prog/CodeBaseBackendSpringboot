package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TelemedicineSessionRepository extends JpaRepository<TelemedicineSession, Long> {

    // Find by session ID
    Optional<TelemedicineSession> findBySessionId(String sessionId);

    // Find by status
    List<TelemedicineSession> findByStatus(SessionStatus status);
    Page<TelemedicineSession> findByStatus(SessionStatus status, Pageable pageable);

    // Find active sessions
    List<TelemedicineSession> findByStatusIn(List<SessionStatus> statuses);

    @Query("SELECT s FROM TelemedicineSession s WHERE s.status = 'ACTIVE'")
    List<TelemedicineSession> findActiveSessions();

    // Find by patient
    Page<TelemedicineSession> findByPatientId(Long patientId, Pageable pageable);
    List<TelemedicineSession> findByPatientIdAndStatus(Long patientId, SessionStatus status);

    // Find by doctor
    Page<TelemedicineSession> findByDoctorId(Long doctorId, Pageable pageable);
    List<TelemedicineSession> findByDoctorIdAndStatus(Long doctorId, SessionStatus status);

    @Query("SELECT COUNT(s) FROM TelemedicineSession s WHERE s.doctor.id = :doctorId AND s.status = 'ACTIVE'")
    Integer countActiveSessionsByDoctorId(@Param("doctorId") Long doctorId);

    // Find by date range
    @Query("SELECT s FROM TelemedicineSession s WHERE s.startTime BETWEEN :startDate AND :endDate")
    List<TelemedicineSession> findByDateRange(
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") OffsetDateTime endDate
    );

    // Find by platform
    Page<TelemedicineSession> findByPlatform(PlatformType platform, Pageable pageable);

    // Find by priority
    List<TelemedicineSession> findByPriorityOrderByStartTimeAsc(Priority priority);

    // Search functionality
    @Query("SELECT s FROM TelemedicineSession s WHERE " +
           "LOWER(s.patient.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.patient.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.doctor.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.doctor.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.sessionId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<TelemedicineSession> searchSessions(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Statistics queries
    @Query("SELECT COUNT(s) FROM TelemedicineSession s WHERE s.status = :status")
    Integer countByStatus(@Param("status") SessionStatus status);

    @Query("SELECT COUNT(DISTINCT s.doctor.id) FROM TelemedicineSession s WHERE s.status = 'ACTIVE'")
    Integer countActiveDoctors();

    @Query("SELECT AVG(s.duration) FROM TelemedicineSession s WHERE s.status = 'COMPLETED'")
    Double calculateAverageDuration();

    @Query("SELECT AVG(s.rating) FROM TelemedicineSession s WHERE s.rating IS NOT NULL")
    Double calculateAverageRating();

    @Query("SELECT SUM(s.actualCost) FROM TelemedicineSession s WHERE " +
           "s.status = 'COMPLETED' AND s.paymentStatus = 'PAID' AND " +
           "s.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenue(
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") OffsetDateTime endDate
    );

    // Advanced filters
    @Query("SELECT s FROM TelemedicineSession s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:platform IS NULL OR s.platform = :platform) AND " +
           "(:priority IS NULL OR s.priority = :priority) AND " +
           "(:doctorId IS NULL OR s.doctor.id = :doctorId) AND " +
           "(:startDate IS NULL OR s.startTime >= :startDate) AND " +
           "(:endDate IS NULL OR s.startTime <= :endDate)")
    Page<TelemedicineSession> findWithFilters(
        @Param("status") SessionStatus status,
        @Param("platform") PlatformType platform,
        @Param("priority") Priority priority,
        @Param("doctorId") Long doctorId,
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") OffsetDateTime endDate,
        Pageable pageable
    );

    // Find sessions requiring reminders
    @Query("SELECT s FROM TelemedicineSession s WHERE " +
           "s.status = 'SCHEDULED' AND s.reminderSent = false AND " +
           "s.startTime BETWEEN :now AND :reminderTime")
    List<TelemedicineSession> findSessionsNeedingReminders(
        @Param("now") OffsetDateTime now,
        @Param("reminderTime") OffsetDateTime reminderTime
    );

    // Revenue by specialty
    @Query("SELECT d.specialty.name, SUM(s.actualCost), COUNT(s), AVG(s.actualCost) " +
           "FROM TelemedicineSession s JOIN s.doctor d " +
           "WHERE s.status = 'COMPLETED' AND s.paymentStatus = 'PAID' " +
           "GROUP BY d.specialty.name " +
           "ORDER BY SUM(s.actualCost) DESC")
    List<Object[]> findRevenueBySpecialty();

    // Platform statistics
    @Query("SELECT s.platform, COUNT(s), AVG(s.duration) " +
           "FROM TelemedicineSession s " +
           "WHERE s.status = 'COMPLETED' " +
           "GROUP BY s.platform")
    List<Object[]> findPlatformStatistics();
}
