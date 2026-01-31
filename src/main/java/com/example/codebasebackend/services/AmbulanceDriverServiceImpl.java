package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.AmbulanceDriver;
import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.repositories.AmbulanceDriverRepository;
import com.example.codebasebackend.repositories.AmbulanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AmbulanceDriverServiceImpl implements AmbulanceDriverService {

    private final AmbulanceDriverRepository driverRepository;
    private final AmbulanceRepository ambulanceRepository;

    @Override
    public AmbulanceDriver addDriver(AmbulanceDriver driver) {
        return driverRepository.save(driver);
    }

    @Override
    public List<AmbulanceDriver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public AmbulanceDriver getDriverById(Long id) {
        return driverRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Driver not found"));
    }

    @Override
    public List<AmbulanceDriver> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers();
    }

    @Override
    public List<AmbulanceDriver> getDriversByStatus(String status) {
        try {
            AmbulanceDriver.DriverStatus driverStatus = AmbulanceDriver.DriverStatus.valueOf(status.toUpperCase());
            return driverRepository.findByStatus(driverStatus);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public AmbulanceDriver updateDriver(Long id, AmbulanceDriver driver) {
        AmbulanceDriver existingDriver = getDriverById(id);
        existingDriver.setName(driver.getName());
        existingDriver.setPhone(driver.getPhone());
        existingDriver.setEmail(driver.getEmail());
        existingDriver.setLicenseNumber(driver.getLicenseNumber());
        existingDriver.setYearsOfExperience(driver.getYearsOfExperience());
        existingDriver.setShiftStart(driver.getShiftStart());
        existingDriver.setShiftEnd(driver.getShiftEnd());
        existingDriver.setEmergencyContact(driver.getEmergencyContact());
        existingDriver.setDateOfBirth(driver.getDateOfBirth());
        existingDriver.setHireDate(driver.getHireDate());
        return driverRepository.save(existingDriver);
    }

    @Override
    @Transactional
    public AmbulanceDriver updateDriverStatus(Long id, String status) {
        AmbulanceDriver driver = getDriverById(id);
        try {
            AmbulanceDriver.DriverStatus driverStatus = AmbulanceDriver.DriverStatus.valueOf(status.toUpperCase());
            driver.setStatus(driverStatus);
            return driverRepository.save(driver);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public AmbulanceDriver assignToAmbulance(Long driverId, Long ambulanceId) {
        AmbulanceDriver driver = getDriverById(driverId);
        Ambulances ambulance = ambulanceRepository.findById(ambulanceId)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ambulance not found"));

        driver.setCurrentAmbulance(ambulance);
        ambulance.setCurrentDriver(driver);

        ambulanceRepository.save(ambulance);
        return driverRepository.save(driver);
    }

    @Override
    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Driver not found");
        }
        driverRepository.deleteById(id);
    }
}
