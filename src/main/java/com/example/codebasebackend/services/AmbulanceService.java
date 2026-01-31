package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.Entities.AmbulanceDispatch;
import com.example.codebasebackend.Entities.AmbulanceTracking;
import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.AssistanceResponse;
import com.example.codebasebackend.dto.request.LocationUpdateRequest;
import com.example.codebasebackend.dto.response.AmbulanceStatistics;

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

    AssistanceResponse createDispatch(AssistanceRequest request);

    List<AssistanceResponse> getAllDispatches();

    AssistanceResponse getDispatchById(Long id);

    AssistanceResponse updateDispatch(Long id, AssistanceRequest request);

    void deleteDispatch(Long id);

    AssistanceResponse trackDispatch(Long id);

    // ==================== NEW METHODS ====================
    // Status management
    List<Ambulances> getAvailableAmbulances();
    List<Ambulances> getAmbulancesByStatus(String status);
    Ambulances updateStatus(Long id, String status);

    // Location & tracking
    AmbulanceTracking updateLocation(Long id, LocationUpdateRequest request);
    List<AmbulanceTracking> getTrackingHistory(Long id, OffsetDateTime from, OffsetDateTime to);
    AmbulanceTracking getCurrentLocation(Long id);
    List<AmbulanceTracking> getAllActiveTracking();

    // Maintenance
    List<Ambulances> getMaintenanceDue();

    // Statistics
    AmbulanceStatistics getStatistics();

    // Search
    List<Ambulances> searchAmbulances(String query);
    List<Ambulances> getAmbulancesByType(String type);

    // Dispatch history
    List<AmbulanceDispatch> getDispatchHistory(Long id);
}
