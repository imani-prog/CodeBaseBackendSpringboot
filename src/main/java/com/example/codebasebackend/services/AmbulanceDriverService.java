package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.AmbulanceDriver;
import java.util.List;

public interface AmbulanceDriverService {
    AmbulanceDriver addDriver(AmbulanceDriver driver);
    List<AmbulanceDriver> getAllDrivers();
    AmbulanceDriver getDriverById(Long id);
    List<AmbulanceDriver> getAvailableDrivers();
    List<AmbulanceDriver> getDriversByStatus(String status);
    AmbulanceDriver updateDriver(Long id, AmbulanceDriver driver);
    AmbulanceDriver updateDriverStatus(Long id, String status);
    AmbulanceDriver assignToAmbulance(Long driverId, Long ambulanceId);
    void deleteDriver(Long id);
}
