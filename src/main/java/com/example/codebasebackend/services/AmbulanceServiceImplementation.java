package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.AssistanceResponse;
import com.example.codebasebackend.Entities.AmbulanceDispatch;
import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.repositories.AmbulanceDispatchRepository;
import com.example.codebasebackend.repositories.AmbulancesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AmbulanceServiceImplementation implements AmbulanceService {

    private final AmbulanceDispatchRepository dispatchRepository;
    private final AmbulancesRepository ambulancesRepository;

    @Override
    public AssistanceResponse createDispatch(AssistanceRequest request) {
        // Fetch an available ambulance
        List<Ambulances> availableAmbulances = ambulancesRepository.findByStatus("AVAILABLE");
        if (availableAmbulances.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "No available ambulances");
        }

        Ambulances selectedAmbulance = availableAmbulances.get(0); // Select the first available ambulance
        selectedAmbulance.setStatus("BUSY"); // Mark the ambulance as busy
        ambulancesRepository.save(selectedAmbulance);

        AmbulanceDispatch dispatch = new AmbulanceDispatch();
        dispatch.setIncidentId(request.getIncidentType());
        dispatch.setPickupLatitude(request.getPickupLatitude());
        dispatch.setPickupLongitude(request.getPickupLongitude());
        dispatch.setAmbulance(selectedAmbulance); // Associate the ambulance
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
