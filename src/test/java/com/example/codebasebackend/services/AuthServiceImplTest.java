package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.Entities.User;
import com.example.codebasebackend.Entities.UserRole;
import com.example.codebasebackend.configs.JwtUtil;
import com.example.codebasebackend.dto.AuthResponse;
import com.example.codebasebackend.dto.LoginRequest;
import com.example.codebasebackend.dto.RegisterRequest;
import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import com.example.codebasebackend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private CommunityHealthWorkersRepository communityHealthWorkersRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerProvisionsPatientProfileWhenRoleIsPatient() {
        RegisterRequest request = buildRegisterRequest(UserRole.PATIENT);
        RegisterRequest.PatientProfileRequest patientProfile = new RegisterRequest.PatientProfileRequest();
        patientProfile.setFirstName("Timothy");
        patientProfile.setLastName("Imani");
        patientProfile.setGender(Patient.Gender.MALE);
        request.setPatient(patientProfile);

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(patientCaptor.capture());
        verify(communityHealthWorkersRepository, never()).save(any(CommunityHealthWorkers.class));
        assertEquals(UserRole.PATIENT, patientCaptor.getValue().getUser().getRole());
        assertEquals("Timothy", patientCaptor.getValue().getFirstName());
        assertEquals("Imani", patientCaptor.getValue().getLastName());
        assertEquals("PATIENT", response.getRole());
    }

    @Test
    void registerProvisionsChwProfileWhenRoleIsChw() {
        RegisterRequest request = buildRegisterRequest(UserRole.CHW);
        RegisterRequest.ChwProfileRequest chwProfile = new RegisterRequest.ChwProfileRequest();
        chwProfile.setFirstName("Mary");
        chwProfile.setLastName("Awino");
        chwProfile.setStatus(CommunityHealthWorkers.Status.AVAILABLE);
        request.setCommunityHealthWorker(chwProfile);

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<CommunityHealthWorkers> chwCaptor = ArgumentCaptor.forClass(CommunityHealthWorkers.class);
        verify(communityHealthWorkersRepository).save(chwCaptor.capture());
        verify(patientRepository, never()).save(any(Patient.class));
        assertEquals(UserRole.CHW, chwCaptor.getValue().getUser().getRole());
        assertEquals("Mary", chwCaptor.getValue().getFirstName());
        assertEquals("Awino", chwCaptor.getValue().getLastName());
        assertEquals("CHW", response.getRole());
    }

    @Test
    void registerDefaultsRoleToPatientAndStillProvisionsProfile() {
        RegisterRequest request = buildRegisterRequest(null);
        request.setPatient(new RegisterRequest.PatientProfileRequest());
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        authService.register(request);

        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(patientCaptor.capture());
        assertEquals(UserRole.PATIENT, patientCaptor.getValue().getUser().getRole());
        assertEquals("timothy", patientCaptor.getValue().getFirstName());
        assertEquals("", patientCaptor.getValue().getLastName());
    }

    @Test
    void loginUpdatesLastLoginAtWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("timothy");
        request.setPassword("Timo123");

        User user = User.builder()
                .id(9L)
                .username("timothy")
                .role(UserRole.PATIENT)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(org.mockito.Mockito.mock(Authentication.class));
        when(userRepository.findByUsernameIgnoreCase("timothy")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("timothy", "PATIENT")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(user.getLastLoginAt());
        verify(userRepository).save(user);
        assertEquals("jwt-token", response.getToken());
    }

    private RegisterRequest buildRegisterRequest(UserRole role) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("timothy");
        request.setEmail("timothyimani6@gmail.com");
        request.setPassword("Timo123");
        request.setFullName("Timothy Imani");
        request.setPhone("0743669252");
        request.setRole(role);
        return request;
    }
}

