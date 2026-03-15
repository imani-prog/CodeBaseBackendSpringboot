package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Doctor;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.Entities.Pharmacy;
import com.example.codebasebackend.Entities.Prescription;
import com.example.codebasebackend.Entities.PrescriptionRefillRequest;
import com.example.codebasebackend.dto.PharmacyRequest;
import com.example.codebasebackend.dto.PharmacyResponse;
import com.example.codebasebackend.dto.PrescriptionRefillRequestPayload;
import com.example.codebasebackend.dto.PrescriptionRefillResponse;
import com.example.codebasebackend.dto.PrescriptionRequest;
import com.example.codebasebackend.dto.PrescriptionResponse;
import com.example.codebasebackend.dto.RefillDecisionRequest;
import com.example.codebasebackend.repositories.DoctorRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import com.example.codebasebackend.repositories.PharmacyRepository;
import com.example.codebasebackend.repositories.PrescriptionRefillRequestRepository;
import com.example.codebasebackend.repositories.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImplementation implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionRefillRequestRepository refillRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final PharmacyRepository pharmacyRepository;

    @Override
    public PrescriptionResponse create(PrescriptionRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));

        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        validateDates(request.getStartDate(), request.getEndDate());
        validateRefills(request.getTotalRefills(), request.getRefillsRemaining());
        applyRelations(prescription, request);
        applyPayload(prescription, request);
        prescription.setPrescriptionCode(resolveCode(request.getPrescriptionCode()));

        Prescription saved = prescriptionRepository.save(prescription);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse get(Long id) {
        return prescriptionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Prescription not found"));
    }

    @Override
    public PrescriptionResponse update(Long id, PrescriptionRequest request) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Prescription not found"));

        if (!prescription.getPatient().getId().equals(request.getPatientId())) {
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
            prescription.setPatient(patient);
        }

        validateDates(request.getStartDate(), request.getEndDate());
        validateRefills(request.getTotalRefills(), request.getRefillsRemaining());
        applyRelations(prescription, request);
        applyPayload(prescription, request);
        if (request.getPrescriptionCode() != null && !request.getPrescriptionCode().isBlank()) {
            prescription.setPrescriptionCode(request.getPrescriptionCode());
        }

        Prescription saved = prescriptionRepository.save(prescription);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Prescription not found");
        }
        prescriptionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrescriptionResponse> search(Long patientId, String status, String searchTerm, int page, int size) {
        String search = (searchTerm != null && !searchTerm.isBlank()) ? searchTerm : null;
        Pageable pageable = PageRequest.of(page, size, Sort.by("prescribedDate").descending());
        return prescriptionRepository.search(patientId, mapStatuses(status), search, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> listByPatientAndStatus(Long patientId, String status) {
        List<Prescription.PrescriptionStatus> statuses = mapStatuses(status);
        List<Prescription> data = (statuses == null)
                ? prescriptionRepository.findByPatientIdOrderByPrescribedDateDesc(patientId)
                : prescriptionRepository.findByPatientIdAndStatusInOrderByPrescribedDateDesc(patientId, statuses);

        return data.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PrescriptionResponse markCompleted(Long id) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Prescription not found"));
        p.setStatus(Prescription.PrescriptionStatus.COMPLETED);
        return toResponse(prescriptionRepository.save(p));
    }

    @Override
    public PrescriptionResponse markExpired(Long id) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Prescription not found"));
        p.setStatus(Prescription.PrescriptionStatus.EXPIRED);
        return toResponse(prescriptionRepository.save(p));
    }

    @Override
    public PrescriptionRefillResponse requestRefill(PrescriptionRefillRequestPayload request) {
        Prescription prescription = prescriptionRepository.findById(request.getPrescriptionId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Prescription not found"));

        if (prescription.getRefillsRemaining() == null || prescription.getRefillsRemaining() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "No refills remaining");
        }

        Pharmacy pharmacy = null;
        if (request.getPharmacyId() != null) {
            pharmacy = pharmacyRepository.findById(request.getPharmacyId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pharmacy not found"));
        }

        PrescriptionRefillRequest refill = new PrescriptionRefillRequest();
        refill.setPrescription(prescription);
        refill.setPharmacy(pharmacy);
        refill.setDeliveryMethod(parseDeliveryMethod(request.getDeliveryMethod()));
        refill.setAdditionalInstructions(request.getAdditionalInstructions());
        refill.setStatus(PrescriptionRefillRequest.RefillStatus.PENDING);

        prescription.setStatus(Prescription.PrescriptionStatus.REFILL_PENDING);
        PrescriptionRefillRequest saved = refillRepository.save(refill);
        prescriptionRepository.save(prescription);

        return toRefillResponse(saved);
    }

    @Override
    public PrescriptionRefillResponse decideRefill(Long refillRequestId, RefillDecisionRequest decisionRequest) {
        PrescriptionRefillRequest refill = refillRepository.findById(refillRequestId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Refill request not found"));

        if (refill.getStatus() != PrescriptionRefillRequest.RefillStatus.PENDING) {
            throw new ResponseStatusException(BAD_REQUEST, "Refill request already processed");
        }

        String decision = decisionRequest.getDecision().trim().toUpperCase();
        Prescription prescription = refill.getPrescription();

        switch (decision) {
            case "APPROVE" -> {
                if (prescription.getRefillsRemaining() == null || prescription.getRefillsRemaining() <= 0) {
                    throw new ResponseStatusException(BAD_REQUEST, "No refills remaining");
                }
                refill.setStatus(PrescriptionRefillRequest.RefillStatus.APPROVED);
                refill.setDecidedAt(OffsetDateTime.now());
                prescription.setRefillsRemaining(prescription.getRefillsRemaining() - 1);
                prescription.setStatus(Prescription.PrescriptionStatus.ACTIVE);
            }
            case "REJECT" -> {
                refill.setStatus(PrescriptionRefillRequest.RefillStatus.REJECTED);
                refill.setDecidedAt(OffsetDateTime.now());
                prescription.setStatus(Prescription.PrescriptionStatus.ACTIVE);
            }
            case "FULFILL" -> {
                refill.setStatus(PrescriptionRefillRequest.RefillStatus.FULFILLED);
                refill.setDecidedAt(OffsetDateTime.now());
                prescription.setStatus(Prescription.PrescriptionStatus.ACTIVE);
            }
            default -> throw new ResponseStatusException(BAD_REQUEST, "Unknown decision");
        }

        PrescriptionRefillRequest saved = refillRepository.save(refill);
        prescriptionRepository.save(prescription);
        return toRefillResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionRefillResponse> listRefills(Long prescriptionId) {
        return refillRepository.findByPrescriptionIdOrderByRequestedAtDesc(prescriptionId)
                .stream()
                .map(this::toRefillResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PharmacyResponse savePharmacy(PharmacyRequest request) {
        Pharmacy pharmacy = request.getId() != null
                ? pharmacyRepository.findById(request.getId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pharmacy not found"))
                : new Pharmacy();

        pharmacy.setName(request.getName());
        pharmacy.setAddress(request.getAddress());
        pharmacy.setCity(request.getCity());
        pharmacy.setPostalCode(request.getPostalCode());
        pharmacy.setPhone(request.getPhone());
        pharmacy.setHours(request.getHours());
        pharmacy.setDistanceText(request.getDistanceText());
        pharmacy.setRating(request.getRating());
        pharmacy.setDeliveryFee(request.getDeliveryFee());
        pharmacy.setEstimatedDelivery(request.getEstimatedDelivery());
        pharmacy.setNhifAccepted(request.getNhifAccepted());
        pharmacy.setOffersDelivery(request.getOffersDelivery());
        pharmacy.setServices(request.getServices() != null ? new ArrayList<>(request.getServices()) : new ArrayList<>());

        Pharmacy saved = pharmacyRepository.save(pharmacy);
        return toPharmacyResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponse> listPharmacies() {
        return pharmacyRepository.findAll().stream()
                .map(this::toPharmacyResponse)
                .collect(Collectors.toList());
    }

    private void applyPayload(Prescription prescription, PrescriptionRequest request) {
        prescription.setMedicationName(request.getMedicationName());
        prescription.setGenericName(request.getGenericName());
        prescription.setDosage(request.getDosage());
        prescription.setFrequency(request.getFrequency());
        prescription.setInstructions(request.getInstructions());
        prescription.setPurpose(request.getPurpose());
        prescription.setWarnings(request.getWarnings());
        prescription.setSideEffects(request.getSideEffects() != null ? new ArrayList<>(request.getSideEffects()) : new ArrayList<>());
        prescription.setPrescribedDate(request.getPrescribedDate());
        prescription.setStartDate(request.getStartDate());
        prescription.setEndDate(request.getEndDate());
        prescription.setTotalRefills(request.getTotalRefills() != null ? request.getTotalRefills() : 0);
        prescription.setRefillsRemaining(request.getRefillsRemaining() != null ? request.getRefillsRemaining() : prescription.getTotalRefills());
        prescription.setProgressPercent(request.getProgressPercent() != null ? request.getProgressPercent() : 0);
        prescription.setNextDoseAt(request.getNextDoseAt());
        prescription.setReminderEnabled(request.getReminderEnabled());
        prescription.setProviderSpecialty(request.getProviderSpecialty());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            prescription.setStatus(parseStatus(request.getStatus()));
        }
    }

    private void applyRelations(Prescription prescription, PrescriptionRequest request) {
        if (request.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Doctor not found"));
            prescription.setPrescribedBy(doctor);
        } else {
            prescription.setPrescribedBy(null);
        }

        if (request.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
            prescription.setHospital(hospital);
        } else {
            prescription.setHospital(null);
        }

        if (request.getPharmacyId() != null) {
            Pharmacy pharmacy = pharmacyRepository.findById(request.getPharmacyId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pharmacy not found"));
            prescription.setPreferredPharmacy(pharmacy);
        } else {
            prescription.setPreferredPharmacy(null);
        }
    }

    private PrescriptionResponse toResponse(Prescription p) {
        PrescriptionResponse dto = new PrescriptionResponse();
        dto.setId(p.getId());
        dto.setPrescriptionCode(p.getPrescriptionCode());
        dto.setMedicationName(p.getMedicationName());
        dto.setGenericName(p.getGenericName());
        dto.setDosage(p.getDosage());
        dto.setFrequency(p.getFrequency());
        dto.setInstructions(p.getInstructions());
        dto.setPurpose(p.getPurpose());
        dto.setWarnings(p.getWarnings());
        dto.setSideEffects(p.getSideEffects());
        dto.setPrescribedDate(p.getPrescribedDate());
        dto.setStartDate(p.getStartDate());
        dto.setEndDate(p.getEndDate());
        dto.setTotalRefills(p.getTotalRefills());
        dto.setRefillsRemaining(p.getRefillsRemaining());
        dto.setProgressPercent(p.getProgressPercent());
        dto.setNextDoseAt(p.getNextDoseAt());
        dto.setReminderEnabled(p.getReminderEnabled());
        dto.setStatus(p.getStatus() != null ? p.getStatus().name() : null);

        dto.setPatientId(p.getPatient() != null ? p.getPatient().getId() : null);
        if (p.getPatient() != null) {
            dto.setPatientName(buildFullName(p.getPatient().getFirstName(), p.getPatient().getMiddleName(), p.getPatient().getLastName()));
        }

        dto.setDoctorId(p.getPrescribedBy() != null ? p.getPrescribedBy().getId() : null);
        dto.setDoctorName(p.getPrescribedBy() != null ? p.getPrescribedBy().getFullName() : null);
        dto.setProviderSpecialty(p.getProviderSpecialty());

        dto.setHospitalId(p.getHospital() != null ? p.getHospital().getId() : null);
        dto.setHospitalName(p.getHospital() != null ? p.getHospital().getName() : null);

        dto.setPharmacyId(p.getPreferredPharmacy() != null ? p.getPreferredPharmacy().getId() : null);
        dto.setPharmacyName(p.getPreferredPharmacy() != null ? p.getPreferredPharmacy().getName() : null);

        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }

    private PrescriptionRefillResponse toRefillResponse(PrescriptionRefillRequest r) {
        PrescriptionRefillResponse dto = new PrescriptionRefillResponse();
        dto.setId(r.getId());
        dto.setPrescriptionId(r.getPrescription() != null ? r.getPrescription().getId() : null);
        dto.setPrescriptionCode(r.getPrescription() != null ? r.getPrescription().getPrescriptionCode() : null);
        dto.setMedicationName(r.getPrescription() != null ? r.getPrescription().getMedicationName() : null);
        dto.setPharmacyId(r.getPharmacy() != null ? r.getPharmacy().getId() : null);
        dto.setPharmacyName(r.getPharmacy() != null ? r.getPharmacy().getName() : null);
        dto.setDeliveryMethod(r.getDeliveryMethod() != null ? r.getDeliveryMethod().name() : null);
        dto.setStatus(r.getStatus() != null ? r.getStatus().name() : null);
        dto.setAdditionalInstructions(r.getAdditionalInstructions());
        dto.setRequestedAt(r.getRequestedAt());
        dto.setDecidedAt(r.getDecidedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }

    private PharmacyResponse toPharmacyResponse(Pharmacy p) {
        PharmacyResponse dto = new PharmacyResponse();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setAddress(p.getAddress());
        dto.setCity(p.getCity());
        dto.setPostalCode(p.getPostalCode());
        dto.setPhone(p.getPhone());
        dto.setHours(p.getHours());
        dto.setDistanceText(p.getDistanceText());
        dto.setRating(p.getRating());
        dto.setDeliveryFee(p.getDeliveryFee());
        dto.setEstimatedDelivery(p.getEstimatedDelivery());
        dto.setNhifAccepted(p.getNhifAccepted());
        dto.setOffersDelivery(p.getOffersDelivery());
        dto.setServices(p.getServices());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }

    private Prescription.PrescriptionStatus parseStatus(String status) {
        try {
            return Prescription.PrescriptionStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid prescription status");
        }
    }

    private List<Prescription.PrescriptionStatus> mapStatuses(String status) {
        if (status == null || status.isBlank()) return null;
        if ("active".equalsIgnoreCase(status)) {
            return List.of(Prescription.PrescriptionStatus.ACTIVE, Prescription.PrescriptionStatus.REFILL_PENDING);
        }
        if ("history".equalsIgnoreCase(status)) {
            return List.of(Prescription.PrescriptionStatus.COMPLETED,
                    Prescription.PrescriptionStatus.EXPIRED,
                    Prescription.PrescriptionStatus.CANCELED);
        }
        return List.of(parseStatus(status));
    }

    private PrescriptionRefillRequest.DeliveryMethod parseDeliveryMethod(String method) {
        try {
            return PrescriptionRefillRequest.DeliveryMethod.valueOf(method.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid delivery method");
        }
    }

    private String resolveCode(String provided) {
        if (provided != null && !provided.isBlank()) return provided;
        return "RX-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private void validateDates(java.time.LocalDate start, java.time.LocalDate end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new ResponseStatusException(BAD_REQUEST, "endDate must be after startDate");
        }
    }

    private void validateRefills(Integer totalRefills, Integer refillsRemaining) {
        if (totalRefills != null && refillsRemaining != null && refillsRemaining > totalRefills) {
            throw new ResponseStatusException(BAD_REQUEST, "refillsRemaining cannot exceed totalRefills");
        }
    }

    private String buildFullName(String firstName, String middleName, String lastName) {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) sb.append(firstName);
        if (middleName != null && !middleName.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(middleName);
        }
        if (lastName != null && !lastName.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(lastName);
        }
        return sb.toString();
    }
}




