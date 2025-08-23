package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Hospital;

import java.util.List;

public interface HospitalService {
    Hospital createHospital(Hospital hospital);

    List<Hospital> listHospitals();

    Hospital getHospital(Long id);

    Hospital getHospitalByCode(String code);

    Hospital updateHospital(Long id, Hospital hospital);

    void deleteHospital(Long id);
}
