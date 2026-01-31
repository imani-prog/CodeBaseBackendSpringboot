package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.AmbulanceDispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface AmbulanceDispatchRepository extends JpaRepository<AmbulanceDispatch, Long> {
    Optional<AmbulanceDispatch> findByIncidentId(String incidentId);
    List<AmbulanceDispatch> findByStatus(AmbulanceDispatch.DispatchStatus status);
    List<AmbulanceDispatch> findByPriority(AmbulanceDispatch.DispatchPriority priority);
    List<AmbulanceDispatch> findByHospitalId(Long hospitalId);
    List<AmbulanceDispatch> findByPatientId(Long patientId);
    List<AmbulanceDispatch> findByRequestTimeBetween(OffsetDateTime from, OffsetDateTime to);

    long countByHospitalIdAndStatusIn(Long hospitalId, List<AmbulanceDispatch.DispatchStatus> statuses);

    @Query("SELECT d FROM AmbulanceDispatch d WHERE d.ambulance.id = :ambulanceId " +
           "ORDER BY d.requestTime DESC")
    List<AmbulanceDispatch> findByAmbulanceIdOrderByRequestTimeDesc(@Param("ambulanceId") Long ambulanceId);

    @Query("SELECT d FROM AmbulanceDispatch d WHERE d.ambulance.vehiclePlate = :vehiclePlate " +
           "ORDER BY d.requestTime DESC")
    List<AmbulanceDispatch> findByVehiclePlate(@Param("vehiclePlate") String vehiclePlate);

    @Query("SELECT COUNT(d) FROM AmbulanceDispatch d WHERE " +
           "d.requestTime >= :from AND d.requestTime <= :to")
    long countDispatchesBetween(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);

    @Query("SELECT d FROM AmbulanceDispatch d WHERE d.status IN :activeStatuses " +
           "ORDER BY d.priority DESC, d.requestTime ASC")
    List<AmbulanceDispatch> findActiveDispatches(@Param("activeStatuses") List<AmbulanceDispatch.DispatchStatus> activeStatuses);

    @Query("SELECT AVG(d.actualResponseTimeMinutes) FROM AmbulanceDispatch d " +
           "WHERE d.actualResponseTimeMinutes IS NOT NULL AND d.ambulance.id = :ambulanceId")
    Double getAverageResponseTimeForAmbulance(@Param("ambulanceId") Long ambulanceId);
}
