package com.example.codebasebackend.services;

import com.example.codebasebackend.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImplementation implements PatientService {

    private final PatientRepository patientRepo;

    @Override
    public void updateLocation(Long patientId, BigDecimal latitude, BigDecimal longitude) {
        if (patientId == null) throw new ResponseStatusException(BAD_REQUEST, "patientId required");
        if (latitude == null || longitude == null) throw new ResponseStatusException(BAD_REQUEST, "lat/lon required");
        var p = patientRepo.findById(patientId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
        p.setLatitude(latitude);
        p.setLongitude(longitude);
        patientRepo.save(p);
    }
}
