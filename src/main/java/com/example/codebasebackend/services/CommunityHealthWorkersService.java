package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.dto.CommunityHealthWorkerRequest;
import com.example.codebasebackend.dto.CommunityHealthWorkerResponse;
import com.example.codebasebackend.dto.PerformanceMetricsRequest;
import org.springframework.data.domain.Page;

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

    // New methods for performance and filtering
    CommunityHealthWorkerResponse updatePerformanceMetrics(Long id, PerformanceMetricsRequest request);
    List<CommunityHealthWorkerResponse> findByRegion(String region);
    List<CommunityHealthWorkerResponse> findByStatus(CommunityHealthWorkers.Status status);
    Page<CommunityHealthWorkerResponse> search(String region, CommunityHealthWorkers.Status status, String city,
                                                int page, int size, String sortBy, String sortDirection);
}


