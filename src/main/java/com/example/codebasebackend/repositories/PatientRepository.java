package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByNationalId(String nationalId);
    List<Patient> findByLastNameIgnoreCase(String lastName);
    List<Patient> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    List<Patient> findByHospitalId(Long hospitalId);
}
