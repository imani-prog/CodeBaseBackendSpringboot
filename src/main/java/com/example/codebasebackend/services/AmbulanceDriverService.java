package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.AmbulanceDriver;
import com.example.codebasebackend.dto.response.AmbulanceDriverResponse;
import java.util.List;

public interface AmbulanceDriverService {
    AmbulanceDriverResponse addDriver(AmbulanceDriver driver);
    List<AmbulanceDriverResponse> getAllDrivers();
    AmbulanceDriverResponse getDriverById(Long id);
    List<AmbulanceDriverResponse> getAvailableDrivers();
    List<AmbulanceDriverResponse> getDriversByStatus(String status);
    AmbulanceDriverResponse updateDriver(Long id, AmbulanceDriver driver);
    AmbulanceDriverResponse updateDriverStatus(Long id, String status);
    AmbulanceDriverResponse assignToAmbulance(Long driverId, Long ambulanceId);
    AmbulanceDriverResponse unassignFromAmbulance(Long driverId);
    void deleteDriver(Long id);
}
