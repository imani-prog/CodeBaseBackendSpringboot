package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.RemittanceAdviceRequest;
import com.example.codebasebackend.dto.RemittanceAdviceResponse;

import java.util.List;

public interface RemittanceAdviceService {
    RemittanceAdviceResponse create(RemittanceAdviceRequest request);
    List<RemittanceAdviceResponse> listByClaim(Long claimId);
}

