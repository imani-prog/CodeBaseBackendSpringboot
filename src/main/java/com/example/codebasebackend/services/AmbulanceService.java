package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.dto.AssistanceRequest;
import com.example.codebasebackend.dto.AssistanceResponse;

import java.util.List;

public interface AmbulanceService {
    Ambulances addAmbulance(Ambulances ambulance);

    List<Ambulances> getAllAmbulances();

    Ambulances getAmbulanceById(Long id);

    Ambulances getAmbulanceByVehiclePlate(String vehiclePlate);

    Ambulances updateAmbulance(Long id, Ambulances ambulance);

    Ambulances updateAmbulanceByVehiclePlate(String vehiclePlate, Ambulances ambulance);

    void deleteAmbulance(Long id);

    AssistanceResponse createDispatch(AssistanceRequest request);

    List<AssistanceResponse> getAllDispatches();

    AssistanceResponse getDispatchById(Long id);

    AssistanceResponse updateDispatch(Long id, AssistanceRequest request);

    void deleteDispatch(Long id);

    AssistanceResponse trackDispatch(Long id);
}
