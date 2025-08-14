package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.IntegrationPartner;
import com.example.codebasebackend.dto.IntegrationPartnerRequest;
import com.example.codebasebackend.dto.IntegrationPartnerResponse;
import com.example.codebasebackend.repositories.IntegrationPartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class IntegrationServiceImplementation implements IntegrationService {

    private final IntegrationPartnerRepository partnerRepo;

    @Override
    public IntegrationPartnerResponse create(IntegrationPartnerRequest request) {
        IntegrationPartner p = new IntegrationPartner();
        apply(p, request);
        IntegrationPartner saved = partnerRepo.save(p);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationPartnerResponse get(Long id) {
        return partnerRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Partner not found"));
    }

    @Override
    public IntegrationPartnerResponse update(Long id, IntegrationPartnerRequest request) {
        IntegrationPartner p = partnerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Partner not found"));
        apply(p, request);
        IntegrationPartner saved = partnerRepo.save(p);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!partnerRepo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Partner not found");
        partnerRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationPartnerResponse> listByType(IntegrationPartner.PartnerType type) {
        return partnerRepo.findByType(type).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationPartnerResponse> listByStatus(IntegrationPartner.PartnerStatus status) {
        return partnerRepo.findByStatus(status).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationPartnerResponse> searchByName(String q) {
        return partnerRepo.findByNameContainingIgnoreCase(q == null ? "" : q)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private void apply(IntegrationPartner e, IntegrationPartnerRequest r) {
        if (r.getName() != null) e.setName(r.getName());
        if (r.getType() != null) e.setType(parseType(r.getType()));
        if (r.getApiUrl() != null) e.setApiUrl(r.getApiUrl());
        if (r.getApiKey() != null) e.setApiKey(r.getApiKey()); // consider encryption
        if (r.getContactEmail() != null) e.setContactEmail(r.getContactEmail());
        if (r.getStatus() != null) e.setStatus(parseStatus(r.getStatus()));
        if (r.getMetadata() != null) e.setMetadata(r.getMetadata());
        if (e.getType() == null) e.setType(IntegrationPartner.PartnerType.OTHER);
        if (e.getStatus() == null) e.setStatus(IntegrationPartner.PartnerStatus.ACTIVE);
    }

    private IntegrationPartner.PartnerType parseType(String s) {
        try { return IntegrationPartner.PartnerType.valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid partner type"); }
    }

    private IntegrationPartner.PartnerStatus parseStatus(String s) {
        try { return IntegrationPartner.PartnerStatus.valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid partner status"); }
    }

    private IntegrationPartnerResponse toResponse(IntegrationPartner p) {
        IntegrationPartnerResponse dto = new IntegrationPartnerResponse();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setType(p.getType() != null ? p.getType().name() : null);
        dto.setApiUrl(p.getApiUrl());
        dto.setContactEmail(p.getContactEmail());
        dto.setStatus(p.getStatus() != null ? p.getStatus().name() : null);
        dto.setMetadata(p.getMetadata());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }
}
