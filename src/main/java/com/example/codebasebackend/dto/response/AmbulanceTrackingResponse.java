package com.example.codebasebackend.dto.response;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Value
@Builder
public class AmbulanceTrackingResponse {
    Long id;
    Long ambulanceId;
    String vehiclePlate;
    Long dispatchId;

    BigDecimal latitude;
    BigDecimal longitude;
    Integer speed;
    Integer heading;
    Integer altitude;
    Integer accuracy;
    Integer batteryLevel;
    Integer signalStrength;

    String locationAddress;
    OffsetDateTime timestamp;
    Boolean isActive;
}

