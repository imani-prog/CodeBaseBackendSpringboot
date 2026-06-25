package com.example.codebasebackend.configs;

import com.example.codebasebackend.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("patientSecurity")
@RequiredArgsConstructor
public class PatientSecurity {

    private final PatientRepository patientRepository;

    public boolean isOwner(Long patientId, Authentication authentication) {
        if (patientId == null || authentication == null || authentication.getName() == null) {
            return false;
        }

        return patientRepository.findByUserUsername(authentication.getName())
                .map(patient -> patientId.equals(patient.getId()))
                .orElse(false);
    }
}

