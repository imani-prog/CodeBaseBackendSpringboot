package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.AmbulanceDriver;
import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.dto.response.AmbulanceDriverResponse;
import com.example.codebasebackend.repositories.AmbulanceDriverRepository;
import com.example.codebasebackend.repositories.AmbulanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmbulanceDriverServiceImpl implements AmbulanceDriverService {

    private final AmbulanceDriverRepository driverRepository;
    private final AmbulanceRepository ambulanceRepository;

    @Override
    public AmbulanceDriverResponse addDriver(AmbulanceDriver driver) {
        return mapToResponse(driverRepository.save(driver));
    }

    @Override
    public List<AmbulanceDriverResponse> getAllDrivers() {
        return driverRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public AmbulanceDriverResponse getDriverById(Long id) {
        AmbulanceDriver driver = driverRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Driver not found"));
        return mapToResponse(driver);
    }

    private AmbulanceDriver getDriverEntityById(Long id) {
        return driverRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Driver not found"));
    }

    @Override
    public List<AmbulanceDriverResponse> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<AmbulanceDriverResponse> getDriversByStatus(String status) {
        try {
            AmbulanceDriver.DriverStatus driverStatus = AmbulanceDriver.DriverStatus.valueOf(status.toUpperCase());
            return driverRepository.findByStatus(driverStatus).stream().map(this::mapToResponse).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public AmbulanceDriverResponse updateDriver(Long id, AmbulanceDriver driver) {
        AmbulanceDriver existingDriver = getDriverEntityById(id);
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
        return mapToResponse(driverRepository.save(existingDriver));
    }

    @Override
    @Transactional
    public AmbulanceDriverResponse updateDriverStatus(Long id, String status) {
        AmbulanceDriver driver = getDriverEntityById(id);
        try {
            AmbulanceDriver.DriverStatus driverStatus = AmbulanceDriver.DriverStatus.valueOf(status.toUpperCase());
            driver.setStatus(driverStatus);
            return mapToResponse(driverRepository.save(driver));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public AmbulanceDriverResponse assignToAmbulance(Long driverId, Long ambulanceId) {
        AmbulanceDriver driver = getDriverEntityById(driverId);
        Ambulances ambulance = ambulanceRepository.findById(ambulanceId)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ambulance not found"));

        // Idempotent assignment: already linked to each other.
        if (driver.getCurrentAmbulance() != null
            && driver.getCurrentAmbulance().getId().equals(ambulanceId)
            && ambulance.getCurrentDriver() != null
            && ambulance.getCurrentDriver().getId().equals(driverId)) {
            return mapToResponse(driver);
        }

        // If this driver is currently linked to another ambulance, clear that old link.
        if (driver.getCurrentAmbulance() != null
            && !driver.getCurrentAmbulance().getId().equals(ambulanceId)) {
            Ambulances previousAmbulance = driver.getCurrentAmbulance();
            previousAmbulance.setCurrentDriver(null);
            ambulanceRepository.save(previousAmbulance);
        }

        // If this ambulance is currently linked to another driver, clear that old link.
        if (ambulance.getCurrentDriver() != null
            && !ambulance.getCurrentDriver().getId().equals(driverId)) {
            AmbulanceDriver previousDriver = ambulance.getCurrentDriver();
            previousDriver.setCurrentAmbulance(null);
            driverRepository.save(previousDriver);
        }

        driver.setCurrentAmbulance(ambulance);
        ambulance.setCurrentDriver(driver);

        ambulanceRepository.save(ambulance);
        return mapToResponse(driverRepository.save(driver));
    }

    @Override
    @Transactional
    public AmbulanceDriverResponse unassignFromAmbulance(Long driverId) {
        AmbulanceDriver driver = getDriverEntityById(driverId);

        if (driver.getCurrentAmbulance() == null) {
            return mapToResponse(driver);
        }

        Ambulances ambulance = driver.getCurrentAmbulance();
        driver.setCurrentAmbulance(null);

        if (ambulance.getCurrentDriver() != null
            && ambulance.getCurrentDriver().getId().equals(driverId)) {
            ambulance.setCurrentDriver(null);
        }

        ambulanceRepository.save(ambulance);
        return mapToResponse(driverRepository.save(driver));
    }

    @Override
    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Driver not found");
        }
        driverRepository.deleteById(id);
    }

    private AmbulanceDriverResponse mapToResponse(AmbulanceDriver driver) {
        AmbulanceDriverResponse.AmbulanceSummary ambulanceSummary = null;
        if (driver.getCurrentAmbulance() != null) {
            ambulanceSummary = AmbulanceDriverResponse.AmbulanceSummary.builder()
                .id(driver.getCurrentAmbulance().getId())
                .vehiclePlate(driver.getCurrentAmbulance().getVehiclePlate())
                .status(driver.getCurrentAmbulance().getStatus() != null ? driver.getCurrentAmbulance().getStatus().name() : null)
                .currentLocation(driver.getCurrentAmbulance().getCurrentLocation())
                .build();
        }

        return AmbulanceDriverResponse.builder()
            .id(driver.getId())
            .name(driver.getName())
            .licenseNumber(driver.getLicenseNumber())
            .phone(driver.getPhone())
            .email(driver.getEmail())
            .status(driver.getStatus())
            .yearsOfExperience(driver.getYearsOfExperience())
            .shiftStart(driver.getShiftStart())
            .shiftEnd(driver.getShiftEnd())
            .totalTrips(driver.getTotalTrips())
            .rating(driver.getRating())
            .emergencyContact(driver.getEmergencyContact())
            .avatarUrl(driver.getAvatarUrl())
            .dateOfBirth(driver.getDateOfBirth())
            .hireDate(driver.getHireDate())
            .createdAt(driver.getCreatedAt())
            .updatedAt(driver.getUpdatedAt())
            .currentAmbulance(ambulanceSummary)
            .build();
    }
}
