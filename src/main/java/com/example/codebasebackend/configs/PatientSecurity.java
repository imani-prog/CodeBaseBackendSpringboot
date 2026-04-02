package com.example.codebasebackend.configs;

import com.example.codebasebackend.Entities.Patient;
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

        return patientRepository.findById(patientId)
                .map(Patient::getUser)
                .map(user -> user.getUsername() != null && user.getUsername().equals(authentication.getName()))
                .orElse(false);
    }
}

