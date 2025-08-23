package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.repositories.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class HospitalServiceImplementation implements HospitalService {

    private final HospitalRepository hospitalRepo;

    @Override
    public Hospital createHospital(Hospital hospital) {
        if (hospital == null) throw new ResponseStatusException(BAD_REQUEST, "hospital payload required");
        // Numeric PK managed by DB; ignore incoming id and server-manage code
        hospital.setId(null);
        hospital.setCode(null);

        if (hospital.getRegistrationNumber() == null || hospital.getRegistrationNumber().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "registrationNumber is required");
        }
        hospitalRepo.findByRegistrationNumber(hospital.getRegistrationNumber()).ifPresent(existing -> {
            throw new ResponseStatusException(BAD_REQUEST, "registrationNumber already exists");
        });

        Hospital saved = hospitalRepo.save(hospital);
        if (saved.getCode() == null) {
            saved.setCode(String.format("HS%03d", saved.getId()));
            saved = hospitalRepo.save(saved);
        }
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hospital> listHospitals() {
        return hospitalRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Hospital getHospital(Long id) {
        if (id == null) throw new ResponseStatusException(BAD_REQUEST, "id required");
        return hospitalRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Hospital getHospitalByCode(String code) {
        if (code == null || code.isBlank()) throw new ResponseStatusException(BAD_REQUEST, "code required");
        return hospitalRepo.findByCode(code).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
    }

    @Override
    public Hospital updateHospital(Long id, Hospital patch) {
        if (id == null) throw new ResponseStatusException(BAD_REQUEST, "id required");
        if (patch == null) throw new ResponseStatusException(BAD_REQUEST, "hospital payload required");
        var existing = hospitalRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));

        // Uniqueness for registrationNumber (excluding this record)
        if (patch.getRegistrationNumber() != null && !patch.getRegistrationNumber().isBlank()) {
            hospitalRepo.findByRegistrationNumber(patch.getRegistrationNumber()).ifPresent(other -> {
                if (!other.getId().equals(id)) throw new ResponseStatusException(BAD_REQUEST, "registrationNumber already exists");
            });
        }

        // Preserve server-managed id/code
        existing.setName(patch.getName());
        existing.setType(patch.getType());
        existing.setRegistrationNumber(patch.getRegistrationNumber());
        existing.setTaxId(patch.getTaxId());
        existing.setEmail(patch.getEmail());
        existing.setMainPhone(patch.getMainPhone());
        existing.setAltPhone(patch.getAltPhone());
        existing.setFax(patch.getFax());
        existing.setWebsite(patch.getWebsite());
        existing.setAddressLine1(patch.getAddressLine1());
        existing.setAddressLine2(patch.getAddressLine2());
        existing.setCity(patch.getCity());
        existing.setState(patch.getState());
        existing.setPostalCode(patch.getPostalCode());
        existing.setCountry(patch.getCountry());
        existing.setLatitude(patch.getLatitude());
        existing.setLongitude(patch.getLongitude());
        existing.setAdminContactName(patch.getAdminContactName());
        existing.setAdminContactEmail(patch.getAdminContactEmail());
        existing.setAdminContactPhone(patch.getAdminContactPhone());
        existing.setNumberOfBeds(patch.getNumberOfBeds());
        existing.setNumberOfIcuBeds(patch.getNumberOfIcuBeds());
        existing.setNumberOfAmbulances(patch.getNumberOfAmbulances());
        existing.setServicesOffered(patch.getServicesOffered());
        existing.setDepartments(patch.getDepartments());
        existing.setOperatingHours(patch.getOperatingHours());
        existing.setAcceptedInsurance(patch.getAcceptedInsurance());
        existing.setStatus(patch.getStatus());
        existing.setNotes(patch.getNotes());

        return hospitalRepo.save(existing);
    }

    @Override
    public void deleteHospital(Long id) {
        if (id == null) throw new ResponseStatusException(BAD_REQUEST, "id required");
        if (!hospitalRepo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Hospital not found");
        hospitalRepo.deleteById(id);
    }
}
