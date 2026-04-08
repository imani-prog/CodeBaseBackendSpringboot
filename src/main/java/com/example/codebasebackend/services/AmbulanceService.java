package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.request.LocationUpdateRequest;
import com.example.codebasebackend.dto.response.AmbulanceDispatchResponse;
import com.example.codebasebackend.dto.response.AmbulanceStatistics;
import com.example.codebasebackend.dto.response.AmbulanceTrackingResponse;

import java.time.OffsetDateTime;
import java.util.List;

public interface AmbulanceService {
    Ambulances addAmbulance(Ambulances ambulance);

    List<Ambulances> getAllAmbulances();

    Ambulances getAmbulanceById(Long id);

    Ambulances getAmbulanceByVehiclePlate(String vehiclePlate);

    Ambulances updateAmbulance(Long id, Ambulances ambulance);

    Ambulances updateAmbulanceByVehiclePlate(String vehiclePlate, Ambulances ambulance);

    void deleteAmbulance(Long id);

    AmbulanceDispatchResponse createDispatch(AssistanceRequest request);

    List<AmbulanceDispatchResponse> getAllDispatches();

    AmbulanceDispatchResponse getDispatchById(Long id);

    AmbulanceDispatchResponse updateDispatch(Long id, AssistanceRequest request);

    void deleteDispatch(Long id);

    AmbulanceDispatchResponse trackDispatch(Long id);

    // ==================== NEW METHODS ====================
    // Status management
    List<Ambulances> getAvailableAmbulances();
    List<Ambulances> getAmbulancesByStatus(String status);
    Ambulances updateStatus(Long id, String status);

    // Location & tracking
    AmbulanceTrackingResponse updateLocation(Long id, LocationUpdateRequest request);
    List<AmbulanceTrackingResponse> getTrackingHistory(Long id, OffsetDateTime from, OffsetDateTime to);
    AmbulanceTrackingResponse getCurrentLocation(Long id);
    List<AmbulanceTrackingResponse> getAllActiveTracking();

    // Maintenance
    List<Ambulances> getMaintenanceDue();

    // Statistics
    AmbulanceStatistics getStatistics();

    // Search
    List<Ambulances> searchAmbulances(String query);
    List<Ambulances> getAmbulancesByType(String type);

    // Dispatch history
    List<AmbulanceDispatchResponse> getDispatchHistory(Long id);
}
