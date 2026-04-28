package com.example.codebasebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    private List<KpiItem> kpis;
    private List<Integer> patientTrend;
    private List<Integer> appointmentSlaTrend;
    private List<String> monthLabels;
    private List<ServiceMixItem> serviceMix;
    private List<PipelineItem> patientCarePipeline;
    private List<OverdueQueueItem> overdueQueues;
    private List<InsuranceMixItem> insurancePayerMix;
    private List<FinanceBasicItem> financeBasics;
    private List<FinanceMonthlyItem> financeMonthly;
    private List<InsuranceClaimStatusItem> insuranceClaimStatus;
    private List<String> quickActions;
    private List<String> liveAlerts;
    private List<SystemHealthItem> systemHealthSnapshot;
    private List<TopChwItem> topPerformingChws;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiItem {
        private String label;
        private String value;
        private String delta;
        private String icon;
        private String tone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceMixItem {
        private String label;
        private Integer value;
        private String color;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PipelineItem {
        private String stage;
        private Integer count;
        private Integer progress;
        private String tone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverdueQueueItem {
        private String queue;
        private Integer count;
        private String severity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsuranceMixItem {
        private String label;
        private Integer value;
        private String color;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinanceBasicItem {
        private String label;
        private String value;
        private String tone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinanceMonthlyItem {
        private String month;
        private Double revenue;
        private Double expenses;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsuranceClaimStatusItem {
        private String label;
        private Integer value;
        private String color;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemHealthItem {
        private String label;
        private String value;
        private Boolean ok;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopChwItem {
        private Long id;
        private String name;
        private String region;
        private Integer monthlyVisits;
        private String successRate;
        private String rating;
        private String status;
    }
}

