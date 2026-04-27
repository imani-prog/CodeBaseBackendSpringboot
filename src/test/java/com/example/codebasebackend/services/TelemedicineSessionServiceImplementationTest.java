package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.*;
import com.example.codebasebackend.repositories.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelemedicineSessionServiceImplementationTest {

    @Mock
    private TelemedicineSessionRepository sessionRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TelemedicineSessionServiceImplementation service;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMySessionsReturnsPatientSessions() {
        User user = User.builder().id(7L).username("patientUser").build();
        Patient patient = new Patient();
        patient.setId(22L);
        patient.setFirstName("Ivy");
        patient.setLastName("Smith");
        patient.setEmail("ivy@example.com");
        patient.setPhone("+155555501");

        Specialty specialty = Specialty.builder().id(9L).name("Cardiology").build();
        Doctor doctor = new Doctor();
        doctor.setId(55L);
        doctor.setFirstName("Alex");
        doctor.setLastName("Lee");
        doctor.setPhotoUrl("https://example.com/doc.png");
        doctor.setRating(4.7);
        doctor.setSpecialty(specialty);

        TelemedicineSession session = new TelemedicineSession();
        session.setId(100L);
        session.setSessionId("TM-100");
        session.setPatient(patient);
        session.setDoctor(doctor);
        session.setSessionType(SessionType.CONSULTATION);
        session.setPlatform(PlatformType.VIDEO_CALL);
        session.setStatus(SessionStatus.SCHEDULED);
        session.setPriority(Priority.NORMAL);
        session.setStartTime(OffsetDateTime.now().plusDays(1));
        session.setPlannedDuration(30);
        session.setSymptoms(List.of("cough"));
        session.setChiefComplaint("persistent cough");

        Page<TelemedicineSession> page = new PageImpl<>(List.of(session));

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("patientUser", "n/a"));

        when(userRepository.findByUsernameIgnoreCase("patientUser")).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(7L)).thenReturn(Optional.of(patient));
        when(sessionRepository.findByPatientId(eq(22L), any(Pageable.class))).thenReturn(page);

        Page<?> result = service.getMySessions(null, Pageable.ofSize(10));

        assertThat(result.getContent()).hasSize(1);
        verify(sessionRepository).findByPatientId(eq(22L), any(Pageable.class));
    }

    @Test
    void getMySessionsUsesUpdatedSinceFilter() {
        User user = User.builder().id(4L).username("patientUser").build();
        Patient patient = new Patient();
        patient.setId(3L);
        patient.setFirstName("Kai");
        patient.setLastName("Jones");
        patient.setEmail("kai@example.com");

        TelemedicineSession session = new TelemedicineSession();
        session.setId(11L);
        session.setSessionId("TM-11");
        session.setPatient(patient);
        Doctor doctor = new Doctor();
        doctor.setId(77L);
        doctor.setFirstName("Pat");
        doctor.setLastName("Morgan");
        session.setDoctor(doctor);
        session.setSessionType(SessionType.CONSULTATION);
        session.setPlatform(PlatformType.AUDIO_CALL);
        session.setStatus(SessionStatus.SCHEDULED);
        session.setPriority(Priority.NORMAL);
        session.setStartTime(OffsetDateTime.now().plusHours(4));

        OffsetDateTime updatedSince = OffsetDateTime.now().minusMinutes(5);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("patientUser", "n/a"));

        when(userRepository.findByUsernameIgnoreCase("patientUser")).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(4L)).thenReturn(Optional.of(patient));
        when(sessionRepository.findByPatientIdAndUpdatedAtAfter(eq(3L), eq(updatedSince), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(session)));

        Page<?> result = service.getMySessions(updatedSince, Pageable.ofSize(5));

        assertThat(result.getContent()).hasSize(1);
        verify(sessionRepository).findByPatientIdAndUpdatedAtAfter(eq(3L), eq(updatedSince), any(Pageable.class));
    }

    @Test
    void getMySessionsFailsWhenNoPatientProfile() {
        User user = User.builder().id(88L).username("patientUser").build();
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("patientUser", "n/a"));

        when(userRepository.findByUsernameIgnoreCase("patientUser")).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(88L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMySessions(null, Pageable.ofSize(5)))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("No patient profile found");
    }
}



