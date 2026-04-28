package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.CommunityHealthWorkerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

public interface CommunityHealthWorkerAssignmentRepository extends JpaRepository<CommunityHealthWorkerAssignment, Long> {
    List<CommunityHealthWorkerAssignment> findByPatientId(Long patientId);
    List<CommunityHealthWorkerAssignment> findByChwId(Long chwId);
    List<CommunityHealthWorkerAssignment> findByStatus(CommunityHealthWorkerAssignment.Status status);
    List<CommunityHealthWorkerAssignment> findByAssignmentType(CommunityHealthWorkerAssignment.AssignmentType assignmentType);
    List<CommunityHealthWorkerAssignment> findByPatientIdOrderByAssignedAtDesc(Long patientId);
    List<CommunityHealthWorkerAssignment> findByChwIdOrderByAssignedAtDesc(Long chwId);
    Optional<CommunityHealthWorkerAssignment> findByAppointmentId(Long appointmentId);
    Optional<CommunityHealthWorkerAssignment> findByHomeVisitId(Long homeVisitId);
    void deleteByAppointmentId(Long appointmentId);
    void deleteByHomeVisitId(Long homeVisitId);
    long countByChwIdAndStatusIn(Long chwId, Collection<CommunityHealthWorkerAssignment.Status> statuses);
    long countByStatusIn(Collection<CommunityHealthWorkerAssignment.Status> statuses);
}

