package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.Entities.User;
import com.example.codebasebackend.Entities.UserRole;
import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserProfileProvisioningService {

    private final PatientRepository patientRepository;
    private final CommunityHealthWorkersRepository chwRepository;

    public void provisionProfileForUser(User user) {
        if (user == null || user.getId() == null || user.getRole() == null) {
            return;
        }

        NameParts nameParts = splitName(user.getFullName(), user.getUsername());

        if (user.getRole() == UserRole.PATIENT) {
            provisionPatientProfile(user, nameParts);
            return;
        }

        if (user.getRole() == UserRole.CHW) {
            provisionChwProfile(user, nameParts);
        }
    }

    private void provisionPatientProfile(User user, NameParts nameParts) {
        if (patientRepository.existsByUserId(user.getId())) {
            return;
        }

        String patientEmail = isPatientEmailAvailable(user.getEmail()) ? user.getEmail() : null;

        Patient patient = Patient.builder()
                .user(user)
                .firstName(nameParts.firstName())
                .lastName(nameParts.lastName())
                .email(patientEmail)
                .phone(user.getPhone())
                .gender(Patient.Gender.UNKNOWN)
                .status(Patient.PatientStatus.ACTIVE)
                .preferredLanguage("en")
                .consentToShareData(Boolean.TRUE)
                .smsOptIn(Boolean.FALSE)
                .emailOptIn(Boolean.TRUE)
                .build();

        patientRepository.save(patient);
    }

    private void provisionChwProfile(User user, NameParts nameParts) {
        if (chwRepository.existsByUserId(user.getId())) {
            return;
        }

        CommunityHealthWorkers chw = CommunityHealthWorkers.builder()
                .user(user)
                .firstName(nameParts.firstName())
                .lastName(nameParts.lastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(CommunityHealthWorkers.Status.AVAILABLE)
                .build();

        chwRepository.save(chw);
    }

    private boolean isPatientEmailAvailable(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return patientRepository.findByEmail(email).isEmpty();
    }

    private NameParts splitName(String fullName, String usernameFallback) {
        String candidate = normalize(fullName);
        if (candidate == null) {
            candidate = normalize(usernameFallback);
        }

        if (candidate == null) {
            return new NameParts("User", "Profile");
        }

        String[] parts = candidate.split("\\s+");
        if (parts.length == 1) {
            return new NameParts(capitalize(parts[0]), "User");
        }

        String firstName = capitalize(parts[0]);
        StringBuilder last = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isBlank()) {
                if (last.length() > 0) {
                    last.append(' ');
                }
                last.append(capitalize(parts[i]));
            }
        }

        if (last.length() == 0) {
            last.append("User");
        }

        return new NameParts(firstName, last.toString());
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "User";
        }

        String lower = value.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private record NameParts(String firstName, String lastName) {
    }
}

