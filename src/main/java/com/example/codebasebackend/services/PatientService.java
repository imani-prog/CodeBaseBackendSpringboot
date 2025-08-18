package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Patient;

import java.math.BigDecimal;
import java.util.List;

public interface PatientService {
    void updateLocation(Long patientId, BigDecimal latitude, BigDecimal longitude);

    Patient savePatient(Patient patient);

    List<Patient> listPatients();

    Patient getPatient(Long id);

    Patient updatePatient(Long id, Patient patient);

    void deletePatient(Long id);
}