package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.SpecialtyRequest;
import com.example.codebasebackend.dto.SpecialtyResponse;

import java.util.List;

public interface SpecialtyService {

    SpecialtyResponse createSpecialty(SpecialtyRequest request);

    SpecialtyResponse getSpecialtyById(Long id);

    SpecialtyResponse getSpecialtyByName(String name);

    List<SpecialtyResponse> getAllSpecialties();

    List<SpecialtyResponse> getActiveSpecialties();

    SpecialtyResponse updateSpecialty(Long id, SpecialtyRequest request);

    void deleteSpecialty(Long id);

    SpecialtyResponse activateSpecialty(Long id);

    SpecialtyResponse deactivateSpecialty(Long id);
}
