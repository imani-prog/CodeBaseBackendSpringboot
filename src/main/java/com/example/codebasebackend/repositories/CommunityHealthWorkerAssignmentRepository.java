package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.CommunityHealthWorkerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityHealthWorkerAssignmentRepository extends JpaRepository<CommunityHealthWorkerAssignment, Long> {
    List<CommunityHealthWorkerAssignment> findByPatientId(Long patientId);
    List<CommunityHealthWorkerAssignment> findByChwId(Long chwId);
    List<CommunityHealthWorkerAssignment> findByStatus(CommunityHealthWorkerAssignment.Status status);
}

