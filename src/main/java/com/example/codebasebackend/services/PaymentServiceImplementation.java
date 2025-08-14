package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Billing;
import com.example.codebasebackend.Entities.Payment;
import com.example.codebasebackend.dto.PaymentRequest;
import com.example.codebasebackend.dto.PaymentResponse;
import com.example.codebasebackend.repositories.BillingRepository;
import com.example.codebasebackend.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImplementation implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillingRepository billingRepository;

    @Override
    public PaymentResponse record(PaymentRequest request) {
        Billing b = billingRepository.findById(request.getBillingId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Invoice not found"));
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Amount must be positive");
        }
        Payment p = new Payment();
        p.setBilling(b);
        p.setMethod(parseMethod(request.getMethod()));
        p.setStatus(parseStatus(request.getStatus()));
        p.setAmount(request.getAmount());
        p.setCurrency(request.getCurrency() != null ? request.getCurrency() : b.getCurrency());
        p.setExternalReference(request.getExternalReference());
        p.setNotes(request.getNotes());
        Payment saved = paymentRepository.save(p);

        // Update billing aggregates only if payment completed
        if (saved.getStatus() == Payment.Status.COMPLETED) {
            BigDecimal newPaid = nz(b.getAmountPaid()).add(saved.getAmount());
            b.setAmountPaid(newPaid);
            b.setBalance(nz(b.getTotal()).subtract(newPaid));
            if (b.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                b.setBalance(BigDecimal.ZERO);
                b.setStatus(Billing.InvoiceStatus.PAID);
            } else {
                b.setStatus(Billing.InvoiceStatus.PARTIALLY_PAID);
            }
            billingRepository.save(b);
        }
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> listByBilling(Long billingId) {
        return paymentRepository.findByBillingIdOrderByCreatedAtAsc(billingId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private Payment.Method parseMethod(String s) {
        try { return Payment.Method.valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid payment method"); }
    }

    private Payment.Status parseStatus(String s) {
        try { return Payment.Status.valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid payment status"); }
    }

    private PaymentResponse toResponse(Payment p) {
        PaymentResponse dto = new PaymentResponse();
        dto.setId(p.getId());
        dto.setBillingId(p.getBilling() != null ? p.getBilling().getId() : null);
        dto.setMethod(p.getMethod() != null ? p.getMethod().name() : null);
        dto.setStatus(p.getStatus() != null ? p.getStatus().name() : null);
        dto.setAmount(p.getAmount());
        dto.setCurrency(p.getCurrency());
        dto.setExternalReference(p.getExternalReference());
        dto.setNotes(p.getNotes());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }

    private BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}

