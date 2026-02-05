package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Specialty;
import com.example.codebasebackend.dto.SpecialtyRequest;
import com.example.codebasebackend.dto.SpecialtyResponse;
import com.example.codebasebackend.repositories.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpecialtyServiceImplementation implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    @Override
    public SpecialtyResponse createSpecialty(SpecialtyRequest request) {
        log.info("Creating specialty: {}", request.getName());

        // Check if specialty with same name already exists
        if (specialtyRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Specialty with name '" + request.getName() + "' already exists");
        }

        Specialty specialty = new Specialty();
        specialty.setName(request.getName());
        specialty.setDescription(request.getDescription());
        specialty.setActive(request.getActive() != null ? request.getActive() : true);

        Specialty savedSpecialty = specialtyRepository.save(specialty);
        log.info("Specialty created successfully: {}", savedSpecialty.getId());

        return mapToResponse(savedSpecialty);
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponse getSpecialtyById(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Specialty not found with ID: " + id));
        return mapToResponse(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponse getSpecialtyByName(String name) {
        Specialty specialty = specialtyRepository.findByName(name)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Specialty not found with name: " + name));
        return mapToResponse(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponse> getAllSpecialties() {
        return specialtyRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponse> getActiveSpecialties() {
        return specialtyRepository.findByActiveTrue()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public SpecialtyResponse updateSpecialty(Long id, SpecialtyRequest request) {
        log.info("Updating specialty ID: {}", id);

        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Specialty not found with ID: " + id));

        // Check if name is being changed to a name that already exists
        if (!specialty.getName().equals(request.getName()) &&
            specialtyRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(BAD_REQUEST,
                "Specialty with name '" + request.getName() + "' already exists");
        }

        specialty.setName(request.getName());
        specialty.setDescription(request.getDescription());
        if (request.getActive() != null) {
            specialty.setActive(request.getActive());
        }

        Specialty updatedSpecialty = specialtyRepository.save(specialty);
        log.info("Specialty updated successfully: {}", id);

        return mapToResponse(updatedSpecialty);
    }

    @Override
    public void deleteSpecialty(Long id) {
        log.info("Deleting specialty ID: {}", id);

        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Specialty not found with ID: " + id));

        specialtyRepository.delete(specialty);
        log.info("Specialty deleted successfully: {}", id);
    }

    @Override
    public SpecialtyResponse activateSpecialty(Long id) {
        log.info("Activating specialty ID: {}", id);

        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Specialty not found with ID: " + id));

        specialty.setActive(true);
        Specialty updatedSpecialty = specialtyRepository.save(specialty);
        log.info("Specialty activated successfully: {}", id);

        return mapToResponse(updatedSpecialty);
    }

    @Override
    public SpecialtyResponse deactivateSpecialty(Long id) {
        log.info("Deactivating specialty ID: {}", id);

        Specialty specialty = specialtyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "Specialty not found with ID: " + id));

        specialty.setActive(false);
        Specialty updatedSpecialty = specialtyRepository.save(specialty);
        log.info("Specialty deactivated successfully: {}", id);

        return mapToResponse(updatedSpecialty);
    }

    // Helper method
    private SpecialtyResponse mapToResponse(Specialty specialty) {
        return SpecialtyResponse.builder()
            .id(specialty.getId())
            .name(specialty.getName())
            .description(specialty.getDescription())
            .active(specialty.getActive())
            .createdAt(specialty.getCreatedAt())
            .updatedAt(specialty.getUpdatedAt())
            .build();
    }
}
