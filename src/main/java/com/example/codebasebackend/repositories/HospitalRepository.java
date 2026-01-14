package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findByRegistrationNumber(String registrationNumber);
    Optional<Hospital> findByCode(String code);
    List<Hospital> findByNameContainingIgnoreCase(String name);
    List<Hospital> findByCityIgnoreCase(String city);
    List<Hospital> findByStatus(Hospital.HospitalStatus status);
    
    /**
     * Find hospitals that have a specific facility
     * @param facility The facility to search for (e.g., "LABORATORY", "PHARMACY")
     * @return List of hospitals that have the specified facility in their facilities string
     */
    List<Hospital> findByFacilitiesContaining(String facility);
}
