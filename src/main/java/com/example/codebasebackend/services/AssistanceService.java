package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.AssistanceResponse;

import java.util.List;

public interface AssistanceService {
    AssistanceResponse requestAssistance(AssistanceRequest request);

    List<AssistanceResponse> getAllDispatches();

    AssistanceResponse getDispatchById(Long id);

    AssistanceResponse updateDispatch(Long id, AssistanceRequest request);

    void deleteDispatch(Long id);
}
