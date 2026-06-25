package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.Entities.AmbulanceTracking;
import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.Entities.AmbulanceDispatch;
import com.example.codebasebackend.dto.request.LocationUpdateRequest;
import com.example.codebasebackend.dto.response.AmbulanceDispatchResponse;
import com.example.codebasebackend.dto.response.AmbulanceResponse;
import com.example.codebasebackend.dto.response.AmbulanceStatistics;
import com.example.codebasebackend.dto.response.AmbulanceTrackingResponse;
import com.example.codebasebackend.repositories.AmbulanceRepository;
import com.example.codebasebackend.repositories.AmbulanceDispatchRepository;
import com.example.codebasebackend.repositories.AmbulanceTrackingRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.PatientRepository;
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
@Transactional
public class AmbulanceServiceImplementation implements AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final AmbulanceDispatchRepository dispatchRepository;
    private final AmbulanceTrackingRepository trackingRepository;
    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    public AmbulanceResponse addAmbulance(Ambulances ambulance) {
        return AmbulanceResponse.from(ambulanceRepository.save(ambulance));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmbulanceResponse> getAllAmbulances() {
        return ambulanceRepository.findAll().stream()
                .map(AmbulanceResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AmbulanceResponse getAmbulanceById(Long id) {
        return AmbulanceResponse.from(ambulanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ambulance not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public AmbulanceResponse getAmbulanceByVehiclePlate(String vehiclePlate) {
        return AmbulanceResponse.from(ambulanceRepository.findByVehiclePlateIgnoreCase(vehiclePlate)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ambulance not found")));
    }

    @Override
    public AmbulanceResponse updateAmbulance(Long id, Ambulances ambulance) {
        Ambulances existingAmbulance = ambulanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ambulance not found"));

        if (ambulance.getVehiclePlate() != null) existingAmbulance.setVehiclePlate(ambulance.getVehiclePlate());
        if (ambulance.getCurrentDriver() != null) existingAmbulance.setCurrentDriver(ambulance.getCurrentDriver());
        if (ambulance.getStatus() != null) existingAmbulance.setStatus(ambulance.getStatus());
        if (ambulance.getMedicName() != null) existingAmbulance.setMedicName(ambulance.getMedicName());
        if (ambulance.getNotes() != null) existingAmbulance.setNotes(ambulance.getNotes());
        if (ambulance.getRegistrationNumber() != null) existingAmbulance.setRegistrationNumber(ambulance.getRegistrationNumber());
        if (ambulance.getModel() != null) existingAmbulance.setModel(ambulance.getModel());
        if (ambulance.getYear() != 0) existingAmbulance.setYear(ambulance.getYear());
        if (ambulance.getFuelType() != null) existingAmbulance.setFuelType(ambulance.getFuelType());
        if (ambulance.getCapacity() != 0) existingAmbulance.setCapacity(ambulance.getCapacity());
        existingAmbulance.setEquippedForICU(ambulance.isEquippedForICU());
        existingAmbulance.setGpsEnabled(ambulance.isGpsEnabled());
        if (ambulance.getInsurancePolicyNumber() != null) existingAmbulance.setInsurancePolicyNumber(ambulance.getInsurancePolicyNumber());
        if (ambulance.getInsuranceProvider() != null) existingAmbulance.setInsuranceProvider(ambulance.getInsuranceProvider());
        if (ambulance.getMileage() != null) existingAmbulance.setMileage(ambulance.getMileage());
        if (ambulance.getFuelLevel() != null) existingAmbulance.setFuelLevel(ambulance.getFuelLevel());
        if (ambulance.getType() != null) existingAmbulance.setType(ambulance.getType());

        return AmbulanceResponse.from(ambulanceRepository.save(existingAmbulance));
    }

    @Override
    public AmbulanceResponse updateAmbulanceByVehiclePlate(String vehiclePlate, Ambulances ambulance) {
        Ambulances existingAmbulance = ambulanceRepository.findByVehiclePlateIgnoreCase(vehiclePlate)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ambulance not found"));

        if (ambulance.getVehiclePlate() != null) existingAmbulance.setVehiclePlate(ambulance.getVehiclePlate());
        if (ambulance.getCurrentDriver() != null) existingAmbulance.setCurrentDriver(ambulance.getCurrentDriver());
        if (ambulance.getStatus() != null) existingAmbulance.setStatus(ambulance.getStatus());
        if (ambulance.getMedicName() != null) existingAmbulance.setMedicName(ambulance.getMedicName());
        if (ambulance.getNotes() != null) existingAmbulance.setNotes(ambulance.getNotes());
        if (ambulance.getRegistrationNumber() != null) existingAmbulance.setRegistrationNumber(ambulance.getRegistrationNumber());
        if (ambulance.getModel() != null) existingAmbulance.setModel(ambulance.getModel());
        if (ambulance.getYear() != 0) existingAmbulance.setYear(ambulance.getYear());
        if (ambulance.getFuelType() != null) existingAmbulance.setFuelType(ambulance.getFuelType());
        if (ambulance.getCapacity() != 0) existingAmbulance.setCapacity(ambulance.getCapacity());
        existingAmbulance.setEquippedForICU(ambulance.isEquippedForICU());
        existingAmbulance.setGpsEnabled(ambulance.isGpsEnabled());
        if (ambulance.getInsurancePolicyNumber() != null) existingAmbulance.setInsurancePolicyNumber(ambulance.getInsurancePolicyNumber());
        if (ambulance.getInsuranceProvider() != null) existingAmbulance.setInsuranceProvider(ambulance.getInsuranceProvider());
        if (ambulance.getMileage() != null) existingAmbulance.setMileage(ambulance.getMileage());
        if (ambulance.getFuelLevel() != null) existingAmbulance.setFuelLevel(ambulance.getFuelLevel());
        if (ambulance.getType() != null) existingAmbulance.setType(ambulance.getType());

        return AmbulanceResponse.from(ambulanceRepository.save(existingAmbulance));
    }

    @Override
    public void deleteAmbulance(Long id) {
        if (!ambulanceRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Ambulance not found");
        }
        ambulanceRepository.deleteById(id);
    }

    @Override
    public AmbulanceDispatchResponse createDispatch(AssistanceRequest request) {
        AmbulanceDispatch dispatch = new AmbulanceDispatch();

        Ambulances selectedAmbulance = pickAvailableAmbulanceForDispatch();
        dispatch.setAmbulance(selectedAmbulance);
        selectedAmbulance.setStatus(Ambulances.AmbulanceStatus.DISPATCHED);
        selectedAmbulance.setLastDispatchTime(OffsetDateTime.now());
        ambulanceRepository.save(selectedAmbulance);

        applyRequestToDispatch(dispatch, request);

        AmbulanceDispatch savedDispatch = dispatchRepository.save(dispatch);
        return mapToResponse(savedDispatch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmbulanceDispatchResponse> getAllDispatches() {
        return dispatchRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AmbulanceDispatchResponse getDispatchById(Long id) {
        AmbulanceDispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Dispatch not found"));
        return mapToResponse(dispatch);
    }

    @Override
    public AmbulanceDispatchResponse updateDispatch(Long id, AssistanceRequest request) {
        AmbulanceDispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Dispatch not found"));
        applyRequestToDispatch(dispatch, request);
        return mapToResponse(dispatchRepository.save(dispatch));
    }

    @Override
    public void deleteDispatch(Long id) {
        if (!dispatchRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Dispatch not found");
        }
        dispatchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AmbulanceDispatchResponse trackDispatch(Long id) {
        AmbulanceDispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Dispatch not found"));
        return mapToResponse(dispatch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmbulanceResponse> getAvailableAmbulances() {
        return ambulanceRepository.findByStatus(Ambulances.AmbulanceStatus.AVAILABLE).stream()
                .map(AmbulanceResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmbulanceResponse> getAmbulancesByStatus(String status) {
        try {
            Ambulances.AmbulanceStatus ambulanceStatus = Ambulances.AmbulanceStatus.fromString(status);
            return ambulanceRepository.findByStatus(ambulanceStatus).stream()
                    .map(AmbulanceResponse::from)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
    }

    @Override
    public AmbulanceResponse updateStatus(Long id, String status) {
        Ambulances ambulance = ambulanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ambulance not found"));
        try {
            ambulance.setStatus(Ambulances.AmbulanceStatus.fromString(status));
            return AmbulanceResponse.from(ambulanceRepository.save(ambulance));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
    }

    @Override
    public AmbulanceTrackingResponse updateLocation(Long id, LocationUpdateRequest request) {
        Ambulances ambulance = ambulanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ambulance not found"));

        ambulance.setCurrentLatitude(request.getLatitude());
        ambulance.setCurrentLongitude(request.getLongitude());
        ambulance.setCurrentLocation(request.getLocationAddress());
        ambulanceRepository.save(ambulance);

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
            dispatchRepository.findById(request.getDispatchId()).ifPresent(tracking::setDispatch);
        }

        return mapToResponse(trackingRepository.save(tracking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmbulanceTrackingResponse> getTrackingHistory(Long id, OffsetDateTime from, OffsetDateTime to) {
        if (!ambulanceRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Ambulance not found");
        }
        OffsetDateTime fromTime = from != null ? from : OffsetDateTime.now().minusDays(1);
        OffsetDateTime toTime = to != null ? to : OffsetDateTime.now();
        return trackingRepository.findByAmbulanceIdAndTimestampBetweenOrderByTimestampDesc(id, fromTime, toTime)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AmbulanceTrackingResponse getCurrentLocation(Long id) {
        if (!ambulanceRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Ambulance not found");
        }
        AmbulanceTracking tracking = trackingRepository.findLatestByAmbulanceId(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No tracking data found"));
        return mapToResponse(tracking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmbulanceTrackingResponse> getAllActiveTracking() {
        return trackingRepository.findAllActiveTracking(OffsetDateTime.now().minusHours(1))
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmbulanceResponse> getMaintenanceDue() {
        return ambulanceRepository.findMaintenanceDue(LocalDate.now()).stream()
                .map(AmbulanceResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<AmbulanceResponse> searchAmbulances(String query) {
        return ambulanceRepository.searchAmbulances(query).stream()
                .map(AmbulanceResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmbulanceResponse> getAmbulancesByType(String type) {
        try {
            Ambulances.AmbulanceType ambulanceType = Ambulances.AmbulanceType.fromString(type);
            return ambulanceRepository.findByType(ambulanceType).stream()
                    .map(AmbulanceResponse::from)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type: " + type);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmbulanceDispatchResponse> getDispatchHistory(Long id) {
        if (!ambulanceRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Ambulance not found");
        }
        return dispatchRepository.findByAmbulanceIdOrderByRequestTimeDesc(id)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ==================== PRIVATE HELPERS ====================

    private Ambulances pickAvailableAmbulanceForDispatch() {
        List<Ambulances> noDriverAvailable = ambulanceRepository.findAvailableWithoutDriver();
        if (!noDriverAvailable.isEmpty()) return noDriverAvailable.get(0);

        List<Ambulances> available = ambulanceRepository.findByStatus(Ambulances.AmbulanceStatus.AVAILABLE);
        if (!available.isEmpty()) return available.get(0);

        throw new ResponseStatusException(HttpStatus.CONFLICT, "No available ambulance for dispatch");
    }

    private void applyRequestToDispatch(AmbulanceDispatch dispatch, AssistanceRequest request) {
        dispatch.setIncidentType(request.getIncidentType());
        dispatch.setCallerName(request.getCallerName());
        dispatch.setCallerPhone(request.getCallerPhone());
        dispatch.setNotes(request.getNotes());
        dispatch.setPickupLatitude(request.getPickupLatitude());
        dispatch.setPickupLongitude(request.getPickupLongitude());
        dispatch.setPickupAddressLine1(request.getPickupAddressLine1());
        dispatch.setPickupAddressLine2(request.getPickupAddressLine2());
        dispatch.setPickupCity(request.getPickupCity());
        dispatch.setPickupState(request.getPickupState());
        dispatch.setPickupPostalCode(request.getPickupPostalCode());
        dispatch.setPickupCountry(request.getPickupCountry());
        dispatch.setDropoffAddressLine1(request.getDropoffAddressLine1());
        dispatch.setDropoffAddressLine2(request.getDropoffAddressLine2());
        dispatch.setDropoffCity(request.getDropoffCity());
        dispatch.setDropoffState(request.getDropoffState());
        dispatch.setDropoffPostalCode(request.getDropoffPostalCode());
        dispatch.setDropoffCountry(request.getDropoffCountry());
        dispatch.setDropoffLatitude(request.getDropoffLatitude());
        dispatch.setDropoffLongitude(request.getDropoffLongitude());

        if (request.getPriority() != null && !request.getPriority().isBlank()) {
            try {
                dispatch.setPriority(AmbulanceDispatch.DispatchPriority.valueOf(request.getPriority().trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid dispatch priority: " + request.getPriority());
            }
        }

        if (request.getPatientId() != null) {
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
            dispatch.setPatient(patient);
            dispatch.setPatientName((patient.getFirstName() == null ? "" : patient.getFirstName()) + " "
                    + (patient.getLastName() == null ? "" : patient.getLastName()));
        }

        if (request.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
            dispatch.setHospital(hospital);
        }
    }

    private AmbulanceDispatchResponse mapToResponse(AmbulanceDispatch dispatch) {
        return AmbulanceDispatchResponse.builder()
                .id(dispatch.getId())
                .incidentId(dispatch.getIncidentId())
                .incidentType(dispatch.getIncidentType())
                .status(dispatch.getStatus() != null ? dispatch.getStatus().name() : null)
                .priority(dispatch.getPriority() != null ? dispatch.getPriority().name() : null)
                .requestTime(dispatch.getRequestTime())
                .createdAt(dispatch.getCreatedAt())
                .patientId(dispatch.getPatient() != null ? dispatch.getPatient().getId() : null)
                .hospitalId(dispatch.getHospital() != null ? dispatch.getHospital().getId() : null)
                .ambulanceId(dispatch.getAmbulance() != null ? dispatch.getAmbulance().getId() : null)
                .callerName(dispatch.getCallerName())
                .callerPhone(dispatch.getCallerPhone())
                .notes(dispatch.getNotes())
                .pickupLatitude(dispatch.getPickupLatitude())
                .pickupLongitude(dispatch.getPickupLongitude())
                .pickupAddressLine1(dispatch.getPickupAddressLine1())
                .pickupCity(dispatch.getPickupCity())
                .dropoffLatitude(dispatch.getDropoffLatitude())
                .dropoffLongitude(dispatch.getDropoffLongitude())
                .dropoffAddressLine1(dispatch.getDropoffAddressLine1())
                .dropoffCity(dispatch.getDropoffCity())
                .build();
    }

    private AmbulanceTrackingResponse mapToResponse(AmbulanceTracking tracking) {
        return AmbulanceTrackingResponse.builder()
                .id(tracking.getId())
                .ambulanceId(tracking.getAmbulance() != null ? tracking.getAmbulance().getId() : null)
                .vehiclePlate(tracking.getAmbulance() != null ? tracking.getAmbulance().getVehiclePlate() : null)
                .dispatchId(tracking.getDispatch() != null ? tracking.getDispatch().getId() : null)
                .latitude(tracking.getLatitude())
                .longitude(tracking.getLongitude())
                .speed(tracking.getSpeed())
                .heading(tracking.getHeading())
                .altitude(tracking.getAltitude())
                .accuracy(tracking.getAccuracy())
                .batteryLevel(tracking.getBatteryLevel())
                .signalStrength(tracking.getSignalStrength())
                .locationAddress(tracking.getLocationAddress())
                .timestamp(tracking.getTimestamp())
                .isActive(tracking.getIsActive())
                .build();
    }
}