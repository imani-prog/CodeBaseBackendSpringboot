package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Billing;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.Entities.Patient;
import com.example.codebasebackend.Entities.ServiceOrder;
import com.example.codebasebackend.Entities.ServiceOrderItem;
import com.example.codebasebackend.dto.BillingRequest;
import com.example.codebasebackend.dto.BillingResponse;
import com.example.codebasebackend.dto.ServiceOrderItemRequest;
import com.example.codebasebackend.dto.ServiceOrderItemResponse;
import com.example.codebasebackend.dto.ServiceOrderRequest;
import com.example.codebasebackend.dto.ServiceOrderResponse;
import com.example.codebasebackend.repositories.BillingRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import com.example.codebasebackend.repositories.PatientRepository;
import com.example.codebasebackend.repositories.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceOrderServiceImplementation implements ServiceOrderService {

    private final ServiceOrderRepository orderRepo;
    private final PatientRepository patientRepo;
    private final HospitalRepository hospitalRepo;
    private final BillingService billingService;
    private final BillingRepository billingRepository;

    @Override
    public ServiceOrderResponse placeOrder(ServiceOrderRequest request) {
        Patient patient = patientRepo.findById(request.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Patient not found"));
        Hospital hospital = null;
        if (request.getHospitalId() != null) {
            hospital = hospitalRepo.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "At least one item is required");
        }

        ServiceOrder order = new ServiceOrder();
        order.setPatient(patient);
        order.setHospital(hospital);
        order.setCurrency(request.getCurrency() != null ? request.getCurrency() : "KES");
        order.setNotes(request.getNotes());

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal linesTax = BigDecimal.ZERO;

        for (ServiceOrderItemRequest itemReq : request.getItems()) {
            if (itemReq.getQuantity() == null || itemReq.getQuantity() < 1) {
                throw new ResponseStatusException(BAD_REQUEST, "Quantity must be >= 1");
            }
            if (itemReq.getUnitPrice() == null || itemReq.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(BAD_REQUEST, "Unit price must be >= 0");
            }
            ServiceOrderItem item = new ServiceOrderItem();
            item.setOrder(order);
            item.setServiceName(itemReq.getServiceName());
            item.setServiceCode(itemReq.getServiceCode());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setLineTax(nz(itemReq.getLineTax()));
            // computeDerived runs on persist/update, but compute now to aggregate totals
            BigDecimal lineSub = BigDecimal.valueOf(itemReq.getQuantity()).multiply(itemReq.getUnitPrice());
            BigDecimal lineTax = nz(itemReq.getLineTax());
            subtotal = subtotal.add(lineSub);
            linesTax = linesTax.add(lineTax);
            order.getItems().add(item);
        }

        BigDecimal discount = nz(request.getDiscount());
        BigDecimal tax = request.getTax() != null ? request.getTax() : linesTax;
        BigDecimal total = subtotal.subtract(discount).add(tax);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setTax(tax);
        order.setTotal(total);

        ServiceOrder saved = orderRepo.save(order);

        // Create billing invoice
        BillingRequest billReq = new BillingRequest();
        billReq.setPatientId(patient.getId());
        billReq.setHospitalId(hospital != null ? hospital.getId() : null);
        billReq.setServiceDate(OffsetDateTime.now());
        billReq.setDueDate(OffsetDateTime.now().plusDays(14));
        billReq.setSubtotal(subtotal);
        billReq.setDiscount(discount);
        billReq.setTax(tax);
        billReq.setTotal(total);
        billReq.setCurrency(order.getCurrency());
        billReq.setStatus(Billing.InvoiceStatus.ISSUED.name());
        billReq.setNotes("From ServiceOrder #" + saved.getId());

        BillingResponse createdInvoice = billingService.create(billReq);
        Billing billingRef = billingRepository.getReferenceById(createdInvoice.getId());
        saved.setBilling(billingRef);
        saved.setStatus(ServiceOrder.Status.INVOICED);
        saved = orderRepo.save(saved);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceOrderResponse get(Long id) {
        return orderRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> listByPatient(Long patientId) {
        return orderRepo.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ServiceOrderResponse toResponse(ServiceOrder o) {
        ServiceOrderResponse dto = new ServiceOrderResponse();
        dto.setId(o.getId());
        dto.setPatientId(o.getPatient() != null ? o.getPatient().getId() : null);
        dto.setHospitalId(o.getHospital() != null ? o.getHospital().getId() : null);
        dto.setStatus(o.getStatus() != null ? o.getStatus().name() : null);
        dto.setSubtotal(o.getSubtotal());
        dto.setDiscount(o.getDiscount());
        dto.setTax(o.getTax());
        dto.setTotal(o.getTotal());
        dto.setCurrency(o.getCurrency());
        dto.setNotes(o.getNotes());
        dto.setBillingId(o.getBilling() != null ? o.getBilling().getId() : null);
        dto.setInvoiceNumber(o.getBilling() != null ? o.getBilling().getInvoiceNumber() : null);
        dto.setCreatedAt(o.getCreatedAt());
        dto.setUpdatedAt(o.getUpdatedAt());
        dto.setItems(o.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()));
        return dto;
    }

    private ServiceOrderItemResponse toItemResponse(ServiceOrderItem item) {
        ServiceOrderItemResponse dto = new ServiceOrderItemResponse();
        dto.setId(item.getId());
        dto.setServiceName(item.getServiceName());
        dto.setServiceCode(item.getServiceCode());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setLineSubtotal(item.getLineSubtotal());
        dto.setLineTax(item.getLineTax());
        dto.setLineTotal(item.getLineTotal());
        return dto;
    }

    private BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}

