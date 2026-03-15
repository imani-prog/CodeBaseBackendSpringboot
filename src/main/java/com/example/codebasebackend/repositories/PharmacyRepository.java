package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    List<Pharmacy> findByNameContainingIgnoreCase(String name);
}

