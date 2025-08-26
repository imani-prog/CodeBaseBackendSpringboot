package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Ambulances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmbulancesRepository extends JpaRepository<Ambulances, Long> {

    // Find all ambulances by status
    List<Ambulances> findByStatus(String status);
}
