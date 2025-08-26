package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.CommunityHealthWorkerRequest;
import com.example.codebasebackend.dto.CommunityHealthWorkerResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CommunityHealthWorkersService {
    CommunityHealthWorkerResponse create(CommunityHealthWorkerRequest request);
    CommunityHealthWorkerResponse get(Long id);
    List<CommunityHealthWorkerResponse> list();
    CommunityHealthWorkerResponse update(Long id, CommunityHealthWorkerRequest request);
    void delete(Long id);
    CommunityHealthWorkerResponse updateLocation(Long id, BigDecimal lat, BigDecimal lon);
    CommunityHealthWorkerResponse findNearestAvailable(BigDecimal lat, BigDecimal lon, Long hospitalId);
    CommunityHealthWorkerResponse findNearestAvailable(BigDecimal lat, BigDecimal lon, Long hospitalId, BigDecimal radiusKm);
}
