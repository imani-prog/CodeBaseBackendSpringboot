package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.*;
import com.example.codebasebackend.dto.DoctorRequest;
import com.example.codebasebackend.dto.DoctorResponse;
import com.example.codebasebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DoctorServiceImplementation implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    public DoctorResponse createDoctor(DoctorRequest request) {
        log.info("Creating doctor: {} {}", request.getFirstName(), request.getLastName());


        if (doctorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Doctor with email " + request.getEmail() + " already exists");
        }


        if (doctorRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Doctor with license number " + request.getLicenseNumber() + " already exists");
        }


        Specialty specialty = null;
        if (request.getSpecialtyId() != null) {
            specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Specialty not found with ID: " + request.getSpecialtyId()));
        }


        Hospital hospital = null;
        if (request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Hospital not found with ID: " + request.getHospitalId()));
        }


        String doctorId = generateDoctorId();


        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId);
        doctor.setFirstName(request.getFirstName());
        doctor.setMiddleName(request.getMiddleName());
        doctor.setLastName(request.getLastName());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setAlternativePhone(request.getAlternativePhone());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setSpecialty(specialty);
        doctor.setExperience(request.getExperience());
        doctor.setQualifications(request.getQualifications() != null ? request.getQualifications() : new ArrayList<>());
        doctor.setLanguages(request.getLanguages() != null ? request.getLanguages() : new ArrayList<>());
        doctor.setRating(0.0);
        doctor.setTotalSessions(0);
        doctor.setCompletedSessions(0);
        doctor.setStatus(request.getStatus() != null ? request.getStatus() : DoctorStatus.OFFLINE);
        doctor.setLastStatusUpdate(OffsetDateTime.now());
        doctor.setActive(request.getActive() != null ? request.getActive() : true);
        doctor.setPhotoUrl(request.getPhotoUrl());
        doctor.setBio(request.getBio());
        doctor.setLocation(request.getLocation());
        doctor.setHospital(hospital);

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor created successfully: {}", doctorId);

        return mapToResponse(savedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Doctor not found with ID: " + id));
        return mapToResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorByDoctorId(String doctorId) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Doctor not found with ID: " + doctorId));
        return mapToResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorByEmail(String email) {
        Doctor doctor = doctorRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Doctor not found with email: " + email));
        return mapToResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponse> getAllDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable)
            .map(this::mapToResponse);
    }

    @Override
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Doctor not found with ID: " + id));


        if (!doctor.getEmail().equals(request.getEmail())) {
            if (doctorRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new ResponseStatusException(BAD_REQUEST,
                    "Doctor with email " + request.getEmail() + " already exists");
            }
        }


        if (!doctor.getLicenseNumber().equals(request.getLicenseNumber())) {
            if (doctorRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
                throw new ResponseStatusException(BAD_REQUEST,
                    "Doctor with license number " + request.getLicenseNumber() + " already exists");
            }
        }


        if (request.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Specialty not found with ID: " + request.getSpecialtyId()));
            doctor.setSpecialty(specialty);
        }


        if (request.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                    "Hospital not found with ID: " + request.getHospitalId()));
            doctor.setHospital(hospital);
        }


        doctor.setFirstName(request.getFirstName());
        doctor.setMiddleName(request.getMiddleName());
        doctor.setLastName(request.getLastName());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setAlternativePhone(request.getAlternativePhone());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setExperience(request.getExperience());
        doctor.setQualifications(request.getQualifications());
        doctor.setLanguages(request.getLanguages());
        doctor.setPhotoUrl(request.getPhotoUrl());
        doctor.setBio(request.getBio());
        doctor.setLocation(request.getLocation());

        if (request.getStatus() != null) {
            doctor.updateStatus(request.getStatus());
        }

        if (request.getActive() != null) {
            doctor.setActive(request.getActive());
        }

        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor updated: {}", doctor.getDoctorId());

        return mapToResponse(updatedDoctor);
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Doctor not found with ID: " + id));

        doctorRepository.delete(doctor);
        log.info("Doctor deleted: {}", doctor.getDoctorId());
    }

    @Override
    public DoctorResponse updateDoctorStatus(Long id, DoctorStatus status) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Doctor not found with ID: " + id));

        doctor.updateStatus(status);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor status updated: {} -> {}", doctor.getDoctorId(), status);

        return mapToResponse(updatedDoctor);
    }

    @Override
    public DoctorResponse setDoctorAvailable(Long id) {
        return updateDoctorStatus(id, DoctorStatus.AVAILABLE);
    }

    @Override
    public DoctorResponse setDoctorBusy(Long id) {
        return updateDoctorStatus(id, DoctorStatus.BUSY);
    }

    @Override
    public DoctorResponse setDoctorOffline(Long id) {
        return updateDoctorStatus(id, DoctorStatus.OFFLINE);
    }

    @Override
    public DoctorResponse setDoctorOnBreak(Long id) {
        return updateDoctorStatus(id, DoctorStatus.BREAK);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponse> searchDoctors(String searchTerm, Pageable pageable) {
        return doctorRepository.searchDoctors(searchTerm, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> getDoctorsByStatus(DoctorStatus status) {
        return doctorRepository.findByStatus(status)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponse> getDoctorsBySpecialty(Long specialtyId, Pageable pageable) {
        return doctorRepository.findBySpecialtyId(specialtyId, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> getAvailableDoctors() {
        return doctorRepository.findByStatus(DoctorStatus.AVAILABLE)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> getOnlineDoctors() {
        List<DoctorStatus> onlineStatuses = Arrays.asList(
            DoctorStatus.AVAILABLE,
            DoctorStatus.BUSY,
            DoctorStatus.BREAK
        );
        return doctorRepository.findByActiveTrueAndStatusIn(onlineStatuses)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public DoctorResponse updateDoctorRating(Long id, Integer newRating) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Doctor not found with ID: " + id));

        doctor.updateRating(newRating);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor rating updated: {} -> {}", doctor.getDoctorId(), doctor.getRating());

        return mapToResponse(updatedDoctor);
    }

    @Override
    public void incrementDoctorSessions(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Doctor not found with ID: " + id));

        doctor.incrementTotalSessions();
        doctorRepository.save(doctor);
        log.info("Doctor sessions incremented: {}", doctor.getDoctorId());
    }

    @Override
    public DoctorResponse activateDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Doctor not found with ID: " + id));

        doctor.setActive(true);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor activated: {}", doctor.getDoctorId());

        return mapToResponse(updatedDoctor);
    }

    @Override
    public DoctorResponse deactivateDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Doctor not found with ID: " + id));

        doctor.setActive(false);
        doctor.updateStatus(DoctorStatus.OFFLINE);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor deactivated: {}", doctor.getDoctorId());

        return mapToResponse(updatedDoctor);
    }

    // Helper Methods
    private String generateDoctorId() {
        // Get the highest existing doctor ID number
        String lastDoctorId = doctorRepository.findTopByOrderByIdDesc()
            .map(Doctor::getDoctorId)
            .orElse("DOC-000");

        // Extract the numeric part and increment
        int lastNumber = 0;
        if (lastDoctorId != null && lastDoctorId.startsWith("DOC-")) {
            try {
                lastNumber = Integer.parseInt(lastDoctorId.substring(4));
            } catch (NumberFormatException e) {
                log.warn("Could not parse doctor ID: {}", lastDoctorId);
            }
        }

        // Generate new ID and ensure uniqueness
        String newDoctorId;
        int attempts = 0;
        do {
            lastNumber++;
            newDoctorId = String.format("DOC-%03d", lastNumber);
            attempts++;

            // Safety check to prevent infinite loop
            if (attempts > 1000) {
                throw new ResponseStatusException(INTERNAL_SERVER_ERROR,
                    "Unable to generate unique doctor ID after 1000 attempts");
            }
        } while (doctorRepository.findByDoctorId(newDoctorId).isPresent());

        return newDoctorId;
    }

    private DoctorResponse mapToResponse(Doctor doctor) {
        return DoctorResponse.builder()
            .id(doctor.getId())
            .doctorId(doctor.getDoctorId())

            .firstName(doctor.getFirstName())
            .middleName(doctor.getMiddleName())
            .lastName(doctor.getLastName())
            .fullName(doctor.getFullName())

            .email(doctor.getEmail())
            .phone(doctor.getPhone())
            .alternativePhone(doctor.getAlternativePhone())

            .licenseNumber(doctor.getLicenseNumber())
            .specialtyId(doctor.getSpecialty() != null ? doctor.getSpecialty().getId() : null)
            .specialtyName(doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : null)
            .experience(doctor.getExperience())
            .qualifications(doctor.getQualifications())
            .languages(doctor.getLanguages())

            .rating(doctor.getRating())
            .totalSessions(doctor.getTotalSessions())
            .completedSessions(doctor.getCompletedSessions())

            .status(doctor.getStatus())
            .lastStatusUpdate(doctor.getLastStatusUpdate())
            .active(doctor.getActive())

            .photoUrl(doctor.getPhotoUrl())
            .bio(doctor.getBio())
            .location(doctor.getLocation())

            .hospitalId(doctor.getHospital() != null ? doctor.getHospital().getId() : null)
            .hospitalName(doctor.getHospital() != null ? doctor.getHospital().getName() : null)

            .createdAt(doctor.getCreatedAt())
            .updatedAt(doctor.getUpdatedAt())
            .build();
    }
}
