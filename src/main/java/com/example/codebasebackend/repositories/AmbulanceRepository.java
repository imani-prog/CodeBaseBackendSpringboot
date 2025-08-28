package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Ambulances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulances, Long> {
    // Find by vehicle plate
    Optional<Ambulances> findByVehiclePlateIgnoreCase(String vehiclePlate);
}
