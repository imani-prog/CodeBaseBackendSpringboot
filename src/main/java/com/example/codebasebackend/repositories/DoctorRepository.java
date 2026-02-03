package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Doctor;
import com.example.codebasebackend.Entities.DoctorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByEmail(String email);

    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    Optional<Doctor> findByDoctorId(String doctorId);

    List<Doctor> findByStatus(DoctorStatus status);

    List<Doctor> findByActiveTrue();

    Page<Doctor> findBySpecialtyId(Long specialtyId, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.active = true AND d.status IN :statuses")
    List<Doctor> findByActiveTrueAndStatusIn(@Param("statuses") List<DoctorStatus> statuses);

    @Query("SELECT d FROM Doctor d WHERE " +
           "d.active = true AND " +
           "(LOWER(d.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Doctor> searchDoctors(@Param("searchTerm") String searchTerm, Pageable pageable);
}
