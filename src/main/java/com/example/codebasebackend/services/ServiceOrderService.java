package com.example.codebasebackend.services;

import com.example.codebasebackend.dto.ServiceOrderRequest;
import com.example.codebasebackend.dto.ServiceOrderResponse;

import java.util.List;

public interface ServiceOrderService {
    ServiceOrderResponse placeOrder(ServiceOrderRequest request);
    ServiceOrderResponse get(Long id);
    List<ServiceOrderResponse> listByPatient(Long patientId);
}

