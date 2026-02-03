package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.DoctorStatus;
import com.example.codebasebackend.dto.DoctorRequest;
import com.example.codebasebackend.dto.DoctorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {

    // CRUD Operations
    DoctorResponse createDoctor(DoctorRequest request);
    DoctorResponse getDoctorById(Long id);
    DoctorResponse getDoctorByDoctorId(String doctorId);
    DoctorResponse getDoctorByEmail(String email);
    Page<DoctorResponse> getAllDoctors(Pageable pageable);
    DoctorResponse updateDoctor(Long id, DoctorRequest request);
    void deleteDoctor(Long id);

    // Status Management
    DoctorResponse updateDoctorStatus(Long id, DoctorStatus status);
    DoctorResponse setDoctorAvailable(Long id);
    DoctorResponse setDoctorBusy(Long id);
    DoctorResponse setDoctorOffline(Long id);
    DoctorResponse setDoctorOnBreak(Long id);

    // Search and Filter
    Page<DoctorResponse> searchDoctors(String searchTerm, Pageable pageable);
    List<DoctorResponse> getDoctorsByStatus(DoctorStatus status);
    Page<DoctorResponse> getDoctorsBySpecialty(Long specialtyId, Pageable pageable);
    List<DoctorResponse> getAvailableDoctors();
    List<DoctorResponse> getOnlineDoctors();

    // Performance
    DoctorResponse updateDoctorRating(Long id, Integer newRating);
    void incrementDoctorSessions(Long id);

    // Activation
    DoctorResponse activateDoctor(Long id);
    DoctorResponse deactivateDoctor(Long id);
}
