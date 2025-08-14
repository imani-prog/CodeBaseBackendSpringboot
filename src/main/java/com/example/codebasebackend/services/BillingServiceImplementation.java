package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Billing;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.dto.BillingRequest;
import com.example.codebasebackend.dto.BillingResponse;
import com.example.codebasebackend.repositories.BillingRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class BillingServiceImplementation implements BillingService {

    private final BillingRepository billingRepository;
    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    public BillingResponse create(BillingRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
        Hospital hospital = null;
        if (request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
        }
        Billing b = new Billing();
        b.setPatient(patient);
        b.setHospital(hospital);
        b.setInvoiceNumber(orGenerateInvoiceNumber(request.getInvoiceNumber()));
        b.setIssueDate(OffsetDateTime.now());
        b.setServiceDate(request.getServiceDate());
        b.setDueDate(request.getDueDate());
        b.setSubtotal(nz(request.getSubtotal()));
        b.setDiscount(nz(request.getDiscount()));
        b.setTax(nz(request.getTax()));
        b.setTotal(nz(request.getTotal()));
        b.setAmountPaid(BigDecimal.ZERO);
        b.setBalance(b.getTotal());
        b.setCurrency(request.getCurrency() != null ? request.getCurrency() : "KES");
        if (request.getStatus() != null) {
            b.setStatus(parseStatus(request.getStatus()));
        } else {
            b.setStatus(Billing.InvoiceStatus.ISSUED);
        }
        b.setNotes(request.getNotes());
        Billing saved = billingRepository.save(b);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BillingResponse get(Long id) {
        return billingRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Invoice not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public BillingResponse getByInvoice(String invoiceNumber) {
        return billingRepository.findByInvoiceNumber(invoiceNumber).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Invoice not found"));
    }

    @Override
    public BillingResponse update(Long id, BillingRequest request) {
        Billing b = billingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Invoice not found"));
        if (request.getPatientId() != null && (b.getPatient() == null || !b.getPatient().getId().equals(request.getPatientId()))) {
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
            b.setPatient(patient);
        }
        if (request.getHospitalId() != null) {
            if (b.getHospital() == null || !b.getHospital().getId().equals(request.getHospitalId())) {
                Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
                b.setHospital(hospital);
            }
        }
        if (request.getInvoiceNumber() != null && !request.getInvoiceNumber().isBlank()) b.setInvoiceNumber(request.getInvoiceNumber());
        b.setServiceDate(request.getServiceDate());
        b.setDueDate(request.getDueDate());
        if (request.getSubtotal() != null) b.setSubtotal(request.getSubtotal());
        if (request.getDiscount() != null) b.setDiscount(request.getDiscount());
        if (request.getTax() != null) b.setTax(request.getTax());
        if (request.getTotal() != null) {
            b.setTotal(request.getTotal());
            // Recompute balance if total changed
            b.setBalance(b.getTotal().subtract(nz(b.getAmountPaid())));
        }
        if (request.getCurrency() != null) b.setCurrency(request.getCurrency());
        if (request.getStatus() != null) b.setStatus(parseStatus(request.getStatus()));
        b.setNotes(request.getNotes());
        Billing saved = billingRepository.save(b);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!billingRepository.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Invoice not found");
        billingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingResponse> listByPatient(Long patientId) {
        return billingRepository.findByPatientIdOrderByIssueDateDesc(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingResponse> listByStatus(Billing.InvoiceStatus status) {
        return billingRepository.findByStatusOrderByIssueDateDesc(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private String orGenerateInvoiceNumber(String provided) {
        if (provided != null && !provided.isBlank()) return provided;
        return "INV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private Billing.InvoiceStatus parseStatus(String s) {
        try { return Billing.InvoiceStatus.valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid invoice status"); }
    }

    private BillingResponse toResponse(Billing b) {
        BillingResponse dto = new BillingResponse();
        dto.setId(b.getId());
        dto.setInvoiceNumber(b.getInvoiceNumber());
        dto.setPatientId(b.getPatient() != null ? b.getPatient().getId() : null);
        dto.setHospitalId(b.getHospital() != null ? b.getHospital().getId() : null);
        dto.setIssueDate(b.getIssueDate());
        dto.setServiceDate(b.getServiceDate());
        dto.setDueDate(b.getDueDate());
        dto.setSubtotal(b.getSubtotal());
        dto.setDiscount(b.getDiscount());
        dto.setTax(b.getTax());
        dto.setTotal(b.getTotal());
        dto.setAmountPaid(b.getAmountPaid());
        dto.setBalance(b.getBalance());
        dto.setCurrency(b.getCurrency());
        dto.setStatus(b.getStatus() != null ? b.getStatus().name() : null);
        dto.setNotes(b.getNotes());
        dto.setCreatedAt(b.getCreatedAt());
        dto.setUpdatedAt(b.getUpdatedAt());
        return dto;
    }

    private BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
