package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.InsuranceClaim;
import com.example.codebasebackend.Entities.RemittanceAdvice;
import com.example.codebasebackend.dto.RemittanceAdviceRequest;
import com.example.codebasebackend.dto.RemittanceAdviceResponse;
import com.example.codebasebackend.repositories.InsuranceClaimRepository;
import com.example.codebasebackend.repositories.RemittanceAdviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class RemittanceAdviceServiceImplementation implements RemittanceAdviceService {

    private final RemittanceAdviceRepository remittanceRepo;
    private final InsuranceClaimRepository claimRepo;

    @Override
    public RemittanceAdviceResponse create(RemittanceAdviceRequest request) {
        InsuranceClaim claim = claimRepo.findById(request.getClaimId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Claim not found"));
        RemittanceAdvice r = new RemittanceAdvice();
        r.setClaim(claim);
        r.setAmountPaid(request.getAmountPaid());
        r.setPayerReference(request.getPayerReference());
        r.setRemittanceDate(request.getRemittanceDate());
        r.setAdjustments(request.getAdjustments());
        r.setNotes(request.getNotes());
        RemittanceAdvice saved = remittanceRepo.save(r);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RemittanceAdviceResponse> listByClaim(Long claimId) {
        return remittanceRepo.findByClaimId(claimId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private RemittanceAdviceResponse toResponse(RemittanceAdvice r) {
        RemittanceAdviceResponse dto = new RemittanceAdviceResponse();
        dto.setId(r.getId());
        dto.setClaimId(r.getClaim() != null ? r.getClaim().getId() : null);
        dto.setAmountPaid(r.getAmountPaid());
        dto.setPayerReference(r.getPayerReference());
        dto.setRemittanceDate(r.getRemittanceDate());
        dto.setAdjustments(r.getAdjustments());
        dto.setNotes(r.getNotes());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }
}

