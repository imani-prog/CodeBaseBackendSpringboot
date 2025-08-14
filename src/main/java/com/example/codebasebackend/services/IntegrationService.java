package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.IntegrationPartner;
import com.example.codebasebackend.dto.IntegrationPartnerRequest;
import com.example.codebasebackend.dto.IntegrationPartnerResponse;

import java.util.List;

public interface IntegrationService {
    IntegrationPartnerResponse create(IntegrationPartnerRequest request);
    IntegrationPartnerResponse get(Long id);
    IntegrationPartnerResponse update(Long id, IntegrationPartnerRequest request);
    void delete(Long id);
    List<IntegrationPartnerResponse> listByType(IntegrationPartner.PartnerType type);
    List<IntegrationPartnerResponse> listByStatus(IntegrationPartner.PartnerStatus status);
    List<IntegrationPartnerResponse> searchByName(String q);
}
