package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.Entities.AmbulanceTracking;
import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.AssistanceResponse;
import com.example.codebasebackend.Entities.AmbulanceDispatch;
import com.example.codebasebackend.dto.request.LocationUpdateRequest;
import com.example.codebasebackend.dto.response.AmbulanceStatistics;
import com.example.codebasebackend.repositories.AmbulanceRepository;
import com.example.codebasebackend.repositories.AmbulanceDispatchRepository;
import com.example.codebasebackend.repositories.AmbulanceTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AmbulanceServiceImplementation implements AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final AmbulanceDispatchRepository dispatchRepository;
    private final AmbulanceTrackingRepository trackingRepository;

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

        // Only update fields if they are not null (partial update support)
        if (ambulance.getVehiclePlate() != null) {
            existingAmbulance.setVehiclePlate(ambulance.getVehiclePlate());
        }
        if (ambulance.getDriverName() != null) {
            existingAmbulance.setDriverName(ambulance.getDriverName());
        }
        if (ambulance.getDriverPhone() != null) {
            existingAmbulance.setDriverPhone(ambulance.getDriverPhone());
        }
        if (ambulance.getStatus() != null) {
            existingAmbulance.setStatus(ambulance.getStatus());
        }
        if (ambulance.getMedicName() != null) {
            existingAmbulance.setMedicName(ambulance.getMedicName());
        }
        if (ambulance.getNotes() != null) {
            existingAmbulance.setNotes(ambulance.getNotes());
        }
        if (ambulance.getRegistrationNumber() != null) {
            existingAmbulance.setRegistrationNumber(ambulance.getRegistrationNumber());
        }
        if (ambulance.getModel() != null) {
            existingAmbulance.setModel(ambulance.getModel());
        }
        if (ambulance.getYear() != 0) {  // Year is primitive int, check for non-zero
            existingAmbulance.setYear(ambulance.getYear());
        }
        if (ambulance.getFuelType() != null) {
            existingAmbulance.setFuelType(ambulance.getFuelType());
        }
        if (ambulance.getCapacity() != 0) {  // Capacity is primitive int, check for non-zero
            existingAmbulance.setCapacity(ambulance.getCapacity());
        }
        // For boolean fields, always update as they can't be null
        existingAmbulance.setEquippedForICU(ambulance.isEquippedForICU());
        existingAmbulance.setGpsEnabled(ambulance.isGpsEnabled());

        if (ambulance.getInsurancePolicyNumber() != null) {
            existingAmbulance.setInsurancePolicyNumber(ambulance.getInsurancePolicyNumber());
        }
        if (ambulance.getInsuranceProvider() != null) {
            existingAmbulance.setInsuranceProvider(ambulance.getInsuranceProvider());
        }
        if (ambulance.getMileage() != null) {
            existingAmbulance.setMileage(ambulance.getMileage());
        }
        if (ambulance.getFuelLevel() != null) {
            existingAmbulance.setFuelLevel(ambulance.getFuelLevel());
        }
        if (ambulance.getType() != null) {
            existingAmbulance.setType(ambulance.getType());
        }

        return ambulanceRepository.save(existingAmbulance);
    }

    @Override
    public Ambulances updateAmbulanceByVehiclePlate(String vehiclePlate, Ambulances ambulance) {
        Ambulances existingAmbulance = getAmbulanceByVehiclePlate(vehiclePlate);

        // Only update fields if they are not null (partial update support)
        if (ambulance.getVehiclePlate() != null) {
            existingAmbulance.setVehiclePlate(ambulance.getVehiclePlate());
        }
        if (ambulance.getDriverName() != null) {
            existingAmbulance.setDriverName(ambulance.getDriverName());
        }
        if (ambulance.getDriverPhone() != null) {
            existingAmbulance.setDriverPhone(ambulance.getDriverPhone());
        }
        if (ambulance.getStatus() != null) {
            existingAmbulance.setStatus(ambulance.getStatus());
        }
        if (ambulance.getMedicName() != null) {
            existingAmbulance.setMedicName(ambulance.getMedicName());
        }
        if (ambulance.getNotes() != null) {
            existingAmbulance.setNotes(ambulance.getNotes());
        }
        if (ambulance.getRegistrationNumber() != null) {
            existingAmbulance.setRegistrationNumber(ambulance.getRegistrationNumber());
        }
        if (ambulance.getModel() != null) {
            existingAmbulance.setModel(ambulance.getModel());
        }
        if (ambulance.getYear() != 0) {
            existingAmbulance.setYear(ambulance.getYear());
        }
        if (ambulance.getFuelType() != null) {
            existingAmbulance.setFuelType(ambulance.getFuelType());
        }
        if (ambulance.getCapacity() != 0) {
            existingAmbulance.setCapacity(ambulance.getCapacity());
        }
        // For boolean fields, always update
        existingAmbulance.setEquippedForICU(ambulance.isEquippedForICU());
        existingAmbulance.setGpsEnabled(ambulance.isGpsEnabled());

        if (ambulance.getInsurancePolicyNumber() != null) {
            existingAmbulance.setInsurancePolicyNumber(ambulance.getInsurancePolicyNumber());
        }
        if (ambulance.getInsuranceProvider() != null) {
            existingAmbulance.setInsuranceProvider(ambulance.getInsuranceProvider());
        }
        if (ambulance.getMileage() != null) {
            existingAmbulance.setMileage(ambulance.getMileage());
        }
        if (ambulance.getFuelLevel() != null) {
            existingAmbulance.setFuelLevel(ambulance.getFuelLevel());
        }
        if (ambulance.getType() != null) {
            existingAmbulance.setType(ambulance.getType());
        }

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

    // ==================== NEW METHOD IMPLEMENTATIONS ====================

    @Override
    public List<Ambulances> getAvailableAmbulances() {
        return ambulanceRepository.findByStatus(Ambulances.AmbulanceStatus.AVAILABLE);
    }

    @Override
    public List<Ambulances> getAmbulancesByStatus(String status) {
        try {
            Ambulances.AmbulanceStatus ambulanceStatus = Ambulances.AmbulanceStatus.fromString(status);
            return ambulanceRepository.findByStatus(ambulanceStatus);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public Ambulances updateStatus(Long id, String status) {
        Ambulances ambulance = getAmbulanceById(id);
        try {
            ambulance.setStatus(Ambulances.AmbulanceStatus.fromString(status));
            return ambulanceRepository.save(ambulance);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public AmbulanceTracking updateLocation(Long id, LocationUpdateRequest request) {
        Ambulances ambulance = getAmbulanceById(id);

        // Update ambulance current location
        ambulance.setCurrentLatitude(request.getLatitude());
        ambulance.setCurrentLongitude(request.getLongitude());
        ambulance.setCurrentLocation(request.getLocationAddress());
        ambulanceRepository.save(ambulance);

        // Create tracking record
        AmbulanceTracking tracking = AmbulanceTracking.builder()
            .ambulance(ambulance)
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .speed(request.getSpeed())
            .heading(request.getHeading())
            .batteryLevel(request.getBatteryLevel())
            .signalStrength(request.getSignalStrength())
            .locationAddress(request.getLocationAddress())
            .isActive(true)
            .build();

        if (request.getDispatchId() != null) {
            AmbulanceDispatch dispatch = dispatchRepository.findById(request.getDispatchId())
                .orElse(null);
            tracking.setDispatch(dispatch);
        }

        return trackingRepository.save(tracking);
    }

    @Override
    public List<AmbulanceTracking> getTrackingHistory(Long id, OffsetDateTime from, OffsetDateTime to) {
        if (!ambulanceRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Ambulance not found");
        }

        OffsetDateTime fromTime = from != null ? from : OffsetDateTime.now().minusDays(1);
        OffsetDateTime toTime = to != null ? to : OffsetDateTime.now();

        return trackingRepository.findByAmbulanceIdAndTimestampBetweenOrderByTimestampDesc(id, fromTime, toTime);
    }

    @Override
    public AmbulanceTracking getCurrentLocation(Long id) {
        if (!ambulanceRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Ambulance not found");
        }
        return trackingRepository.findLatestByAmbulanceId(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No tracking data found"));
    }

    @Override
    public List<AmbulanceTracking> getAllActiveTracking() {
        OffsetDateTime since = OffsetDateTime.now().minusHours(1);
        return trackingRepository.findAllActiveTracking(since);
    }

    @Override
    public List<Ambulances> getMaintenanceDue() {
        LocalDate today = LocalDate.now();
        return ambulanceRepository.findMaintenanceDue(today);
    }

    @Override
    public AmbulanceStatistics getStatistics() {
        return AmbulanceStatistics.builder()
            .totalFleet(ambulanceRepository.count())
            .available(ambulanceRepository.countByStatus(Ambulances.AmbulanceStatus.AVAILABLE))
            .busy(ambulanceRepository.countBusy())
            .maintenance(ambulanceRepository.countByStatus(Ambulances.AmbulanceStatus.MAINTENANCE))
            .outOfService(ambulanceRepository.countByStatus(Ambulances.AmbulanceStatus.OUT_OF_SERVICE))
            .averageMileage(ambulanceRepository.getAverageMileage())
            .averageFuelLevel(ambulanceRepository.getAverageFuelLevel())
            .totalDispatches(ambulanceRepository.getTotalDispatches())
            .basicLifeSupport(ambulanceRepository.countByType(Ambulances.AmbulanceType.BASIC_LIFE_SUPPORT))
            .advancedLifeSupport(ambulanceRepository.countByType(Ambulances.AmbulanceType.ADVANCED_LIFE_SUPPORT))
            .criticalCare(ambulanceRepository.countByType(Ambulances.AmbulanceType.CRITICAL_CARE))
            .patientTransport(ambulanceRepository.countByType(Ambulances.AmbulanceType.PATIENT_TRANSPORT))
            .build();
    }

    @Override
    public List<Ambulances> searchAmbulances(String query) {
        return ambulanceRepository.searchAmbulances(query);
    }

    @Override
    public List<Ambulances> getAmbulancesByType(String type) {
        try {
            Ambulances.AmbulanceType ambulanceType = Ambulances.AmbulanceType.fromString(type);
            return ambulanceRepository.findByType(ambulanceType);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type: " + type);
        }
    }

    @Override
    public List<AmbulanceDispatch> getDispatchHistory(Long id) {
        if (!ambulanceRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Ambulance not found");
        }
        return dispatchRepository.findByAmbulanceIdOrderByRequestTimeDesc(id);
    }
}
