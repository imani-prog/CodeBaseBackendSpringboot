package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.AmbulanceEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmbulanceEquipmentRepository extends JpaRepository<AmbulanceEquipment, Long> {

    Optional<AmbulanceEquipment> findByName(String name);

    List<AmbulanceEquipment> findByCategory(AmbulanceEquipment.EquipmentCategory category);

    List<AmbulanceEquipment> findByIsRequiredTrue();
}
