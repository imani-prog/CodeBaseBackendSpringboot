package com.example.codebasebackend.services;

import java.math.BigDecimal;

public interface PatientService {
    void updateLocation(Long patientId, BigDecimal latitude, BigDecimal longitude);
}
