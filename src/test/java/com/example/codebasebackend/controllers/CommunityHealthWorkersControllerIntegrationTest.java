package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.dto.CommunityHealthWorkerRequest;
import com.example.codebasebackend.dto.CommunityHealthWorkerResponse;
import com.example.codebasebackend.dto.PerformanceMetricsRequest;
import com.example.codebasebackend.services.CommunityHealthWorkersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommunityHealthWorkersController.class)
class CommunityHealthWorkersControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommunityHealthWorkersService service;

    private CommunityHealthWorkerResponse testResponse;

    @BeforeEach
    void setUp() {
        testResponse = new CommunityHealthWorkerResponse();
        testResponse.setId(1L);
        testResponse.setCode("CHW001");
        testResponse.setFirstName("Grace");
        testResponse.setMiddleName("Wanjiru");
        testResponse.setLastName("Akinyi");
        testResponse.setEmail("grace@example.com");
        testResponse.setPhone("+254712345678");
        testResponse.setRegion("Nairobi");
        testResponse.setCity("Nairobi");
        testResponse.setAssignedPatients(15);
        testResponse.setStartDate(LocalDate.of(2024, 1, 1));
        testResponse.setMonthlyVisits(45);
        testResponse.setSuccessRate(new BigDecimal("95.50"));
        testResponse.setResponseTime("1.8hrs");
        testResponse.setRating(new BigDecimal("4.8"));
        testResponse.setStatus("AVAILABLE");
        testResponse.setFullName("Grace Wanjiru Akinyi");
        testResponse.setAvatar("GA");
    }

    @Test
    void shouldUpdatePerformanceMetrics() throws Exception {
        // Given
        PerformanceMetricsRequest request = new PerformanceMetricsRequest();
        request.setMonthlyVisits(50);
        request.setSuccessRate(new BigDecimal("96.00"));
        request.setResponseTime("1.5hrs");
        request.setRating(new BigDecimal("4.9"));

        when(service.updatePerformanceMetrics(eq(1L), any(PerformanceMetricsRequest.class)))
                .thenReturn(testResponse);

        // When/Then
        mockMvc.perform(patch("/api/chw/1/performance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.region").value("Nairobi"))
                .andExpect(jsonPath("$.fullName").value("Grace Wanjiru Akinyi"))
                .andExpect(jsonPath("$.avatar").value("GA"));
    }

    @Test
    void shouldGetCHWsByRegion() throws Exception {
        // Given
        List<CommunityHealthWorkerResponse> responses = Arrays.asList(testResponse);
        when(service.findByRegion("Nairobi")).thenReturn(responses);

        // When/Then
        mockMvc.perform(get("/api/chw/by-region/Nairobi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].region").value("Nairobi"))
                .andExpect(jsonPath("$[0].fullName").value("Grace Wanjiru Akinyi"))
                .andExpect(jsonPath("$[0].avatar").value("GA"));
    }

    @Test
    void shouldGetCHWsByStatus() throws Exception {
        // Given
        List<CommunityHealthWorkerResponse> responses = Arrays.asList(testResponse);
        when(service.findByStatus(CommunityHealthWorkers.Status.AVAILABLE)).thenReturn(responses);

        // When/Then
        mockMvc.perform(get("/api/chw/by-status/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$[0].fullName").value("Grace Wanjiru Akinyi"));
    }

    @Test
    void shouldSearchCHWsWithFilters() throws Exception {
        // Given
        Page<CommunityHealthWorkerResponse> page = new PageImpl<>(Arrays.asList(testResponse));
        when(service.search(anyString(), any(), anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/chw/search")
                        .param("region", "Nairobi")
                        .param("status", "AVAILABLE")
                        .param("city", "Nairobi")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].region").value("Nairobi"))
                .andExpect(jsonPath("$.content[0].fullName").value("Grace Wanjiru Akinyi"))
                .andExpect(jsonPath("$.content[0].assignedPatients").value(15));
    }

    @Test
    void shouldValidatePerformanceMetricsRequest() throws Exception {
        // Given - Invalid rating (> 5.0)
        PerformanceMetricsRequest request = new PerformanceMetricsRequest();
        request.setRating(new BigDecimal("6.0")); // Invalid

        // When/Then
        mockMvc.perform(patch("/api/chw/1/performance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateSuccessRate() throws Exception {
        // Given - Invalid success rate (> 100)
        PerformanceMetricsRequest request = new PerformanceMetricsRequest();
        request.setSuccessRate(new BigDecimal("101.00")); // Invalid

        // When/Then
        mockMvc.perform(patch("/api/chw/1/performance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateCHWWithNewFields() throws Exception {
        // Given
        CommunityHealthWorkerRequest request = new CommunityHealthWorkerRequest();
        request.setFirstName("Grace");
        request.setLastName("Akinyi");
        request.setEmail("grace@example.com");
        request.setPhone("+254712345678");
        request.setRegion("Nairobi");
        request.setAssignedPatients(0);
        request.setStartDate(LocalDate.of(2024, 1, 1));

        when(service.create(any(CommunityHealthWorkerRequest.class))).thenReturn(testResponse);

        // When/Then
        mockMvc.perform(post("/api/chw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").value("Nairobi"))
                .andExpect(jsonPath("$.assignedPatients").value(15))
                .andExpect(jsonPath("$.fullName").value("Grace Wanjiru Akinyi"))
                .andExpect(jsonPath("$.avatar").value("GA"));
    }
}

