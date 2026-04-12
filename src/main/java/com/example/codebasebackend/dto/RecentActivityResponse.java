package com.example.codebasebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentActivityResponse {
    private String activityType;
    private String title;
    private String subtitle;
    private OffsetDateTime activityAt;
}

