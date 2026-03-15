package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    boolean existsByPrescriptionCode(String prescriptionCode);

    List<Prescription> findByPatientIdAndStatusInOrderByPrescribedDateDesc(Long patientId,
                                                                           Collection<Prescription.PrescriptionStatus> statuses);

    List<Prescription> findByPatientIdOrderByPrescribedDateDesc(Long patientId);

    @Query("SELECT p FROM Prescription p WHERE " +
           "(:patientId IS NULL OR p.patient.id = :patientId) AND " +
           "(:statuses IS NULL OR p.status IN :statuses) AND " +
           "(:searchTerm IS NULL OR " +
           "  LOWER(p.medicationName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "  LOWER(p.genericName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "  LOWER(p.purpose) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "  LOWER(p.instructions) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY p.prescribedDate DESC")
    Page<Prescription> search(@Param("patientId") Long patientId,
                              @Param("statuses") Collection<Prescription.PrescriptionStatus> statuses,
                              @Param("searchTerm") String searchTerm,
                              Pageable pageable);
}


