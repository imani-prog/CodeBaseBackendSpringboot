package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.HomeVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeVisitRepository extends JpaRepository<HomeVisit, Long> {
    List<HomeVisit> findByPatientIdOrderByScheduledAtDesc(Long patientId);
    List<HomeVisit> findByChwIdOrderByScheduledAtDesc(Long chwId);
    List<HomeVisit> findByStatusOrderByScheduledAtDesc(HomeVisit.Status status);
    long countByScheduledAtGreaterThanEqualAndScheduledAtLessThan(java.time.OffsetDateTime from, java.time.OffsetDateTime to);
}

