package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.HomeVisit;
import com.example.codebasebackend.dto.HomeVisitCancelRequest;
import com.example.codebasebackend.dto.HomeVisitCompleteRequest;
import com.example.codebasebackend.dto.HomeVisitLocationRequest;
import com.example.codebasebackend.dto.HomeVisitRequest;
import com.example.codebasebackend.dto.HomeVisitResponse;
import com.example.codebasebackend.dto.HomeVisitRescheduleRequest;

import java.util.List;

public interface HomeVisitService {
    List<HomeVisitResponse> list(Long patientId, Long chwId, HomeVisit.Status status);
    HomeVisitResponse getById(Long id);
    HomeVisitResponse create(HomeVisitRequest request);
    HomeVisitResponse update(Long id, HomeVisitRequest request);
    HomeVisitResponse complete(Long id, HomeVisitCompleteRequest request);
    HomeVisitResponse cancel(Long id, HomeVisitCancelRequest request);
    HomeVisitResponse reschedule(Long id, HomeVisitRescheduleRequest request);
    HomeVisitResponse updateLocation(Long id, HomeVisitLocationRequest request);
}

