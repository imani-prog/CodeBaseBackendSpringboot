package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.AmbulanceDriver;
import com.example.codebasebackend.Entities.AmbulanceDriver.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmbulanceDriverRepository extends JpaRepository<AmbulanceDriver, Long> {

    Optional<AmbulanceDriver> findByLicenseNumber(String licenseNumber);

    Optional<AmbulanceDriver> findByEmail(String email);

    List<AmbulanceDriver> findByStatus(DriverStatus status);

    long countByStatus(DriverStatus status);

    @Query("SELECT d FROM AmbulanceDriver d WHERE d.status = 'ON_DUTY' AND d.currentAmbulance IS NULL")
    List<AmbulanceDriver> findAvailableDrivers();

    @Query("SELECT d FROM AmbulanceDriver d WHERE d.currentAmbulance.id = :ambulanceId")
    Optional<AmbulanceDriver> findByCurrentAmbulanceId(Long ambulanceId);
}
