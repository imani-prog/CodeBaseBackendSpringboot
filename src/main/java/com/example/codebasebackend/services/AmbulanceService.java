package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.AssistanceResponse;
import java.util.List;

public interface AmbulanceService {
    AssistanceResponse createDispatch(AssistanceRequest request);

    List<AssistanceResponse> getAllDispatches();

    AssistanceResponse getDispatchById(Long id);

    AssistanceResponse updateDispatch(Long id, AssistanceRequest request);

    void deleteDispatch(Long id);

    AssistanceResponse trackDispatch(Long id);
}
