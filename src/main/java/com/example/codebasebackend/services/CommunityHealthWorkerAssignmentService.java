package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.CommunityHealthWorkerAssignment;
import com.example.codebasebackend.Entities.Appointment;
import com.example.codebasebackend.dto.ChwAssignmentReassignRequest;
import com.example.codebasebackend.dto.ChwAssignmentRequest;
import com.example.codebasebackend.dto.ChwAssignmentResponse;

import java.util.List;

public interface CommunityHealthWorkerAssignmentService {
    List<ChwAssignmentResponse> list(Long patientId,
                                     Long chwId,
                                     CommunityHealthWorkerAssignment.Status status,
                                     CommunityHealthWorkerAssignment.AssignmentType assignmentType);

    ChwAssignmentResponse getById(Long id);

    ChwAssignmentResponse create(ChwAssignmentRequest request);

    ChwAssignmentResponse updateStatus(Long id, CommunityHealthWorkerAssignment.Status status);

    ChwAssignmentResponse reassign(Long id, ChwAssignmentReassignRequest request);

    ChwAssignmentResponse replace(Long id, ChwAssignmentRequest request);

    List<ChwAssignmentResponse> listByPatient(Long patientId);

    List<ChwAssignmentResponse> listByChw(Long chwId);

    void syncFromAppointment(Appointment appointment);

    void removeForAppointment(Long appointmentId);
}


