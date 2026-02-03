package com.example.codebasebackend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueDataResponse {

    private BigDecimal daily;
    private BigDecimal weekly;
    private BigDecimal monthly;

    private List<RevenueBySpecialty> bySpecialty;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueBySpecialty {
        private String specialty;
        private BigDecimal revenue;
        private Integer sessions;
        private BigDecimal avgCost;
    }
}
