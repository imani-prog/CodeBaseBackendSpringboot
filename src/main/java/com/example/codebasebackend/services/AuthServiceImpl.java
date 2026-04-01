package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.Entities.User;
import com.example.codebasebackend.Entities.UserRole;
import com.example.codebasebackend.Entities.UserStatus;
import com.example.codebasebackend.configs.JwtUtil;
import com.example.codebasebackend.dto.AuthResponse;
import com.example.codebasebackend.dto.LoginRequest;
import com.example.codebasebackend.dto.RegisterRequest;
import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import com.example.codebasebackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PatientRepository patientRepository;
    private final CommunityHealthWorkersRepository communityHealthWorkersRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserRole role = request.getRole() != null ? request.getRole() : UserRole.PATIENT;

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(user);
        if (role == UserRole.PATIENT) {
            RegisterRequest.PatientProfileRequest patientRequest = request.getPatient();
            if (patientRequest == null) {
                throw new IllegalArgumentException("Patient details are required for PATIENT registration");
            }
            patientRepository.save(buildPatient(saved, patientRequest));
        } else if (role == UserRole.CHW) {
            RegisterRequest.ChwProfileRequest chwRequest = request.getCommunityHealthWorker();
            if (chwRequest == null) {
                throw new IllegalArgumentException("CHW details are required for CHW registration");
            }
            communityHealthWorkersRepository.save(buildChw(saved, chwRequest));
        }

        String token = jwtUtil.generateToken(saved.getUsername(), saved.getRole().name());
        return new AuthResponse(token, saved.getUsername(), saved.getRole().name(), saved.getId());
    }

    private Patient buildPatient(User user, RegisterRequest.PatientProfileRequest request) {
        return Patient.builder()
                .user(user)
                .firstName(firstNameOrUsername(request.getFirstName(), user.getUsername()))
                .middleName(request.getMiddleName())
                .lastName(lastNameOrEmpty(request.getLastName()))
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .phone(request.getPhone())
                .secondaryPhone(request.getSecondaryPhone())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .nationalId(request.getNationalId())
                .insuranceMemberId(request.getInsuranceMemberId())
                .insuranceProviderName(request.getInsuranceProviderName())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactRelation(request.getEmergencyContactRelation())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .allergies(request.getAllergies())
                .medications(request.getMedications())
                .chronicConditions(request.getChronicConditions())
                .bloodType(request.getBloodType())
                .preferredLanguage(request.getPreferredLanguage())
                .status(request.getStatus())
                .maritalStatus(request.getMaritalStatus())
                .consentToShareData(request.getConsentToShareData())
                .smsOptIn(request.getSmsOptIn())
                .emailOptIn(request.getEmailOptIn())
                .notes(request.getNotes())
                .hospital(findHospital(request.getHospitalId()))
                .build();
    }

    private CommunityHealthWorkers buildChw(User user, RegisterRequest.ChwProfileRequest request) {
        return CommunityHealthWorkers.builder()
                .user(user)
                .code(request.getCode())
                .firstName(firstNameOrUsername(request.getFirstName(), user.getUsername()))
                .middleName(request.getMiddleName())
                .lastName(lastNameOrEmpty(request.getLastName()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .region(request.getRegion())
                .assignedPatients(request.getAssignedPatients())
                .startDate(request.getStartDate())
                .responseTime(request.getResponseTime())
                .specialization(request.getSpecialization())
                .status(request.getStatus())
                .successRate(request.getSuccessRate())
                .rating(request.getRating())
                .monthlyVisits(request.getMonthlyVisits())
                .hospital(findHospital(request.getHospitalId()))
                .build();
    }

    private Hospital findHospital(Long hospitalId) {
        if (hospitalId == null) {
            return null;
        }
        return hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found"));
    }

    private String firstNameOrUsername(String firstName, String username) {
        if (firstName == null || firstName.isBlank()) {
            return username;
        }
        return firstName;
    }

    private String lastNameOrEmpty(String lastName) {
        if (lastName == null) {
            return "";
        }
        return lastName;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String loginIdentifier = request.getUsername();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginIdentifier, request.getPassword())
        );

        User user = userRepository.findByUsernameIgnoreCase(loginIdentifier)
                .or(() -> userRepository.findByEmailIgnoreCase(loginIdentifier))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLoginAt(OffsetDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole().name(), user.getId());
    }
}