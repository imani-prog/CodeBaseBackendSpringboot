package com.example.codebasebackend.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DriverSummary {

    Long id;
    String name;
    String status;
    String phone;
}