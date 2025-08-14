package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.AmbulanceDispatch;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
