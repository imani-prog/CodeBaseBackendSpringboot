package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    Optional<Specialty> findByName(String name);

    List<Specialty> findByActiveTrue();

    boolean existsByName(String name);
}
