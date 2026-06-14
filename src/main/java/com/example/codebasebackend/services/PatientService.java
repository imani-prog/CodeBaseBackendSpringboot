package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.dto.PatientResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PatientService {
    void updateLocation(Long patientId, BigDecimal latitude, BigDecimal longitude);
    PatientResponse savePatient(Patient patient);
    List<PatientResponse> listPatients();
    PatientResponse getPatient(Long id);
    PatientResponse updatePatient(Long id, Patient patient);
    void deletePatient(Long id);
}