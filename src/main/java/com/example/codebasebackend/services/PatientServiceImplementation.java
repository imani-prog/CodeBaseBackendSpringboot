package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImplementation implements PatientService {

    private final PatientRepository patientRepo;
    private final HospitalRepository hospitalRepo;

    @Override
    public void updateLocation(Long patientId, BigDecimal latitude, BigDecimal longitude) {
        if (patientId == null) throw new ResponseStatusException(BAD_REQUEST, "patientId required");
        if (latitude == null || longitude == null) throw new ResponseStatusException(BAD_REQUEST, "lat/lon required");
        var p = patientRepo.findById(patientId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
        p.setLatitude(latitude);
        p.setLongitude(longitude);
        patientRepo.save(p);
    }

    @Override
    public Patient savePatient(Patient patient) {
        if (patient == null) throw new ResponseStatusException(BAD_REQUEST, "patient payload required");
        // Ensure we're creating, not updating by ID injection
        patient.setId(null);

        if (patient.getEmail() != null && patientRepo.findByEmail(patient.getEmail()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email already in use");
        }
        if (patient.getNationalId() != null && patientRepo.findByNationalId(patient.getNationalId()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "National ID already in use");
        }

        // Handle optional hospital relationship safely
        if (patient.getHospital() == null || patient.getHospital().getId() == null) {
            // If no hospital id provided, keep it null (optional association)
            patient.setHospital(null);
        } else {
            Long hospitalId = patient.getHospital().getId();
            if (!hospitalRepo.existsById(hospitalId)) {
                throw new ResponseStatusException(BAD_REQUEST, "Hospital with id " + hospitalId + " not found");
            }
            // Attach a reference to avoid a select
            patient.setHospital(hospitalRepo.getReferenceById(hospitalId));
        }

        // Bean validation on controller handles @Valid constraints; prePersist sets defaults.
        return patientRepo.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> listPatients() {
        return patientRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Patient getPatient(Long id) {
        if (id == null) throw new ResponseStatusException(BAD_REQUEST, "id required");
        return patientRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
    }

    @Override
    public Patient updatePatient(Long id, Patient patch) {
        if (id == null) throw new ResponseStatusException(BAD_REQUEST, "id required");
        if (patch == null) throw new ResponseStatusException(BAD_REQUEST, "patient payload required");
        var existing = patientRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));

        // Uniqueness checks for email and nationalId (excluding this record)
        if (patch.getEmail() != null) {
            patientRepo.findByEmail(patch.getEmail()).ifPresent(other -> {
                if (!other.getId().equals(id)) throw new ResponseStatusException(BAD_REQUEST, "Email already in use");
            });
        }
        if (patch.getNationalId() != null) {
            patientRepo.findByNationalId(patch.getNationalId()).ifPresent(other -> {
                if (!other.getId().equals(id)) throw new ResponseStatusException(BAD_REQUEST, "National ID already in use");
            });
        }

        // Copy fields (full update semantics)
        existing.setFirstName(patch.getFirstName());
        existing.setMiddleName(patch.getMiddleName());
        existing.setLastName(patch.getLastName());
        existing.setGender(patch.getGender());
        existing.setDateOfBirth(patch.getDateOfBirth());
        existing.setEmail(patch.getEmail());
        existing.setPhone(patch.getPhone());
        existing.setSecondaryPhone(patch.getSecondaryPhone());
        existing.setAddressLine1(patch.getAddressLine1());
        existing.setAddressLine2(patch.getAddressLine2());
        existing.setCity(patch.getCity());
        existing.setState(patch.getState());
        existing.setPostalCode(patch.getPostalCode());
        existing.setCountry(patch.getCountry());
        existing.setLatitude(patch.getLatitude());
        existing.setLongitude(patch.getLongitude());
        existing.setNationalId(patch.getNationalId());
        existing.setInsuranceMemberId(patch.getInsuranceMemberId());
        existing.setInsuranceProviderName(patch.getInsuranceProviderName());
        existing.setEmergencyContactName(patch.getEmergencyContactName());
        existing.setEmergencyContactRelation(patch.getEmergencyContactRelation());
        existing.setEmergencyContactPhone(patch.getEmergencyContactPhone());
        existing.setAllergies(patch.getAllergies());
        existing.setMedications(patch.getMedications());
        existing.setChronicConditions(patch.getChronicConditions());
        existing.setBloodType(patch.getBloodType());
        existing.setPreferredLanguage(patch.getPreferredLanguage());
        existing.setStatus(patch.getStatus());
        existing.setMaritalStatus(patch.getMaritalStatus());
        existing.setConsentToShareData(patch.getConsentToShareData());
        existing.setSmsOptIn(patch.getSmsOptIn());
        existing.setEmailOptIn(patch.getEmailOptIn());
        existing.setNotes(patch.getNotes());

        // Optional hospital handling
        if (patch.getHospital() == null || patch.getHospital().getId() == null) {
            existing.setHospital(null);
        } else {
            Long hospitalId = patch.getHospital().getId();
            if (!hospitalRepo.existsById(hospitalId)) {
                throw new ResponseStatusException(BAD_REQUEST, "Hospital with id " + hospitalId + " not found");
            }
            existing.setHospital(hospitalRepo.getReferenceById(hospitalId));
        }

        return patientRepo.save(existing);
    }

    @Override
    public void deletePatient(Long id) {
        if (id == null) throw new ResponseStatusException(BAD_REQUEST, "id required");
        if (!patientRepo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Patient not found");
        patientRepo.deleteById(id);
    }
}
