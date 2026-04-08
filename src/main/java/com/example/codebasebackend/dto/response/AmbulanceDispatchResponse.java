package com.example.codebasebackend.dto.response;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Value
@Builder
public class AmbulanceDispatchResponse {
    Long id;
    String incidentId;
    String incidentType;
    String status;
    String priority;
    OffsetDateTime requestTime;
    OffsetDateTime createdAt;

    Long patientId;
    Long hospitalId;
    Long ambulanceId;

    String callerName;
    String callerPhone;
    String notes;

    BigDecimal pickupLatitude;
    BigDecimal pickupLongitude;
    String pickupAddressLine1;
    String pickupCity;

    BigDecimal dropoffLatitude;
    BigDecimal dropoffLongitude;
    String dropoffAddressLine1;
    String dropoffCity;
}

