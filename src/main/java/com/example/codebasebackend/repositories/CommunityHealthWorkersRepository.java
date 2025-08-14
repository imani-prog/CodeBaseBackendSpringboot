package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityHealthWorkersRepository extends JpaRepository<CommunityHealthWorkers, Long> {
    List<CommunityHealthWorkers> findByStatus(CommunityHealthWorkers.Status status);
    List<CommunityHealthWorkers> findByHospitalId(Long hospitalId);
    List<CommunityHealthWorkers> findByStatusAndHospitalId(CommunityHealthWorkers.Status status, Long hospitalId);
}
