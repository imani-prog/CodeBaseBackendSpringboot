package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.AssistanceResponse;

public interface AssistanceService {
    AssistanceResponse requestAssistance(AssistanceRequest request);
}

