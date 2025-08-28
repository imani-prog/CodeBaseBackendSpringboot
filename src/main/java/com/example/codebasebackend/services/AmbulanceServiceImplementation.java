package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.AssistanceResponse;
import com.example.codebasebackend.Entities.AmbulanceDispatch;
import com.example.codebasebackend.repositories.AmbulanceRepository;
import com.example.codebasebackend.repositories.AmbulanceDispatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AmbulanceServiceImplementation implements AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final AmbulanceDispatchRepository dispatchRepository;

    @Override
    public Ambulances addAmbulance(Ambulances ambulance) {
        return ambulanceRepository.save(ambulance);
    }

    @Override
    public List<Ambulances> getAllAmbulances() {
        return ambulanceRepository.findAll();
    }

    @Override
    public Ambulances getAmbulanceById(Long id) {
        return ambulanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ambulance not found"));
    }

    @Override
    public Ambulances getAmbulanceByVehiclePlate(String vehiclePlate) {
        return ambulanceRepository.findByVehiclePlateIgnoreCase(vehiclePlate)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ambulance not found"));
    }

    @Override
    public Ambulances updateAmbulance(Long id, Ambulances ambulance) {
        Ambulances existingAmbulance = getAmbulanceById(id);
        existingAmbulance.setVehiclePlate(ambulance.getVehiclePlate());
        existingAmbulance.setDriverName(ambulance.getDriverName());
        existingAmbulance.setDriverPhone(ambulance.getDriverPhone());
        existingAmbulance.setStatus(ambulance.getStatus());
        existingAmbulance.setMedicName(ambulance.getMedicName());
        existingAmbulance.setNotes(ambulance.getNotes());
        existingAmbulance.setRegistrationNumber(ambulance.getRegistrationNumber());
        existingAmbulance.setModel(ambulance.getModel());
        existingAmbulance.setYear(ambulance.getYear());
        existingAmbulance.setFuelType(ambulance.getFuelType());
        existingAmbulance.setCapacity(ambulance.getCapacity());
        existingAmbulance.setEquippedForICU(ambulance.isEquippedForICU());
        existingAmbulance.setGpsEnabled(ambulance.isGpsEnabled());
        existingAmbulance.setInsurancePolicyNumber(ambulance.getInsurancePolicyNumber());
        existingAmbulance.setInsuranceProvider(ambulance.getInsuranceProvider());
        return ambulanceRepository.save(existingAmbulance);
    }

    @Override
    public Ambulances updateAmbulanceByVehiclePlate(String vehiclePlate, Ambulances ambulance) {
        Ambulances existingAmbulance = getAmbulanceByVehiclePlate(vehiclePlate);
        existingAmbulance.setVehiclePlate(ambulance.getVehiclePlate());
        existingAmbulance.setDriverName(ambulance.getDriverName());
        existingAmbulance.setDriverPhone(ambulance.getDriverPhone());
        existingAmbulance.setStatus(ambulance.getStatus());
        existingAmbulance.setMedicName(ambulance.getMedicName());
        existingAmbulance.setNotes(ambulance.getNotes());
        existingAmbulance.setRegistrationNumber(ambulance.getRegistrationNumber());
        existingAmbulance.setModel(ambulance.getModel());
        existingAmbulance.setYear(ambulance.getYear());
        existingAmbulance.setFuelType(ambulance.getFuelType());
        existingAmbulance.setCapacity(ambulance.getCapacity());
        existingAmbulance.setEquippedForICU(ambulance.isEquippedForICU());
        existingAmbulance.setGpsEnabled(ambulance.isGpsEnabled());
        existingAmbulance.setInsurancePolicyNumber(ambulance.getInsurancePolicyNumber());
        existingAmbulance.setInsuranceProvider(ambulance.getInsuranceProvider());
        return ambulanceRepository.save(existingAmbulance);
    }

    @Override
    public void deleteAmbulance(Long id) {
        if (!ambulanceRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Ambulance not found");
        }
        ambulanceRepository.deleteById(id);
    }

    @Override
    public AssistanceResponse createDispatch(AssistanceRequest request) {
        AmbulanceDispatch dispatch = new AmbulanceDispatch();
        // Map fields from request to dispatch
        dispatch.setIncidentId(request.getIncidentType());
        dispatch.setPickupLatitude(request.getPickupLatitude());
        dispatch.setPickupLongitude(request.getPickupLongitude());
        // Add other mappings as needed

        AmbulanceDispatch savedDispatch = dispatchRepository.save(dispatch);
        return mapToResponse(savedDispatch);
    }

    @Override
    public List<AssistanceResponse> getAllDispatches() {
        return dispatchRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AssistanceResponse getDispatchById(Long id) {
        AmbulanceDispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Dispatch not found"));
        return mapToResponse(dispatch);
    }

    @Override
    public AssistanceResponse updateDispatch(Long id, AssistanceRequest request) {
        AmbulanceDispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Dispatch not found"));
        // Update fields from request
        dispatch.setPickupLatitude(request.getPickupLatitude());
        dispatch.setPickupLongitude(request.getPickupLongitude());
        // Add other updates as needed

        AmbulanceDispatch updatedDispatch = dispatchRepository.save(dispatch);
        return mapToResponse(updatedDispatch);
    }

    @Override
    public void deleteDispatch(Long id) {
        if (!dispatchRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Dispatch not found");
        }
        dispatchRepository.deleteById(id);
    }

    @Override
    public AssistanceResponse trackDispatch(Long id) {
        AmbulanceDispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Dispatch not found"));
        // Logic to track the ambulance (e.g., return current location)
        return mapToResponse(dispatch);
    }

    private AssistanceResponse mapToResponse(AmbulanceDispatch dispatch) {
        AssistanceResponse response = new AssistanceResponse();
        response.setIncidentId(dispatch.getIncidentId());
        response.setPriority(dispatch.getPriority() != null ? dispatch.getPriority().name() : null);
        response.setStatus(dispatch.getStatus().name());
        response.setCreatedAt(dispatch.getRequestTime());
        // Add other mappings as needed
        return response;
    }
}
