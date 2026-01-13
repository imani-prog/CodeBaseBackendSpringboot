package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.dto.CommunityHealthWorkerRequest;
import com.example.codebasebackend.dto.CommunityHealthWorkerResponse;
import com.example.codebasebackend.dto.PerformanceMetricsRequest;
import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityHealthWorkersServiceTest {

    @Mock
    private CommunityHealthWorkersRepository chwRepo;

    @Mock
    private HospitalRepository hospitalRepo;

    @InjectMocks
    private CommunityHealthWorkersServiceImplementation service;

    private CommunityHealthWorkers testChw;
    private CommunityHealthWorkerRequest testRequest;

    @BeforeEach
    void setUp() {
        testChw = CommunityHealthWorkers.builder()
                .id(1L)
                .code("CHW001")
                .firstName("Grace")
                .middleName("Wanjiru")
                .lastName("Akinyi")
                .email("grace@example.com")
                .phone("+254712345678")
                .city("Nairobi")
                .state("Nairobi County")
                .country("Kenya")
                .region("Nairobi")
                .assignedPatients(15)
                .startDate(LocalDate.of(2024, 1, 1))
                .monthlyVisits(45)
                .successRate(new BigDecimal("95.50"))
                .responseTime("1.8hrs")
                .rating(new BigDecimal("4.8"))
                .status(CommunityHealthWorkers.Status.AVAILABLE)
                .build();

        testRequest = new CommunityHealthWorkerRequest();
        testRequest.setFirstName("Grace");
        testRequest.setLastName("Akinyi");
        testRequest.setEmail("grace@example.com");
        testRequest.setPhone("+254712345678");
        testRequest.setRegion("Nairobi");
        testRequest.setAssignedPatients(15);
        testRequest.setStartDate(LocalDate.of(2024, 1, 1));
    }

    @Test
    void shouldCreateCHWWithNewFields() {
        // Given
        CommunityHealthWorkers savedChw = testChw;
        savedChw.setId(1L);
        savedChw.setCode(null); // Code will be set on second save

        CommunityHealthWorkers chwWithCode = testChw;
        chwWithCode.setCode("CHW001");

        when(chwRepo.save(any(CommunityHealthWorkers.class)))
                .thenReturn(savedChw)  // First save
                .thenReturn(chwWithCode); // Second save with code

        // When
        CommunityHealthWorkerResponse response = service.create(testRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRegion()).isEqualTo("Nairobi");
        assertThat(response.getAssignedPatients()).isEqualTo(15);
        assertThat(response.getFullName()).contains("Grace");
        assertThat(response.getAvatar()).isEqualTo("GA");
        verify(chwRepo, atLeast(1)).save(any(CommunityHealthWorkers.class));
    }

    @Test
    void shouldUpdatePerformanceMetrics() {
        // Given
        Long chwId = 1L;
        PerformanceMetricsRequest request = new PerformanceMetricsRequest();
        request.setMonthlyVisits(50);
        request.setSuccessRate(new BigDecimal("95.5"));
        request.setResponseTime("1.5hrs");
        request.setRating(new BigDecimal("4.8"));

        CommunityHealthWorkers updatedChw = testChw;
        updatedChw.setMonthlyVisits(50); // Update to reflect the request
        updatedChw.setResponseTime("1.5hrs");

        when(chwRepo.findById(chwId)).thenReturn(Optional.of(testChw));
        when(chwRepo.save(any(CommunityHealthWorkers.class))).thenReturn(updatedChw);

        // When
        CommunityHealthWorkerResponse response = service.updatePerformanceMetrics(chwId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMonthlyVisits()).isEqualTo(50);
        assertThat(response.getResponseTime()).isEqualTo("1.5hrs");
        verify(chwRepo).save(any(CommunityHealthWorkers.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentCHWPerformance() {
        // Given
        Long chwId = 999L;
        PerformanceMetricsRequest request = new PerformanceMetricsRequest();
        when(chwRepo.findById(chwId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> service.updatePerformanceMetrics(chwId, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("CHW not found");
    }

    @Test
    void shouldFindCHWsByRegion() {
        // Given
        String region = "Nairobi";
        List<CommunityHealthWorkers> chws = Arrays.asList(testChw);
        when(chwRepo.findByRegion(region)).thenReturn(chws);

        // When
        List<CommunityHealthWorkerResponse> results = service.findByRegion(region);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getRegion()).isEqualTo(region);
        assertThat(results.get(0).getFullName()).isNotEmpty();
        assertThat(results.get(0).getAvatar()).isEqualTo("GA");
    }

    @Test
    void shouldFindCHWsByStatus() {
        // Given
        CommunityHealthWorkers.Status status = CommunityHealthWorkers.Status.AVAILABLE;
        List<CommunityHealthWorkers> chws = Arrays.asList(testChw);
        when(chwRepo.findByStatus(status)).thenReturn(chws);

        // When
        List<CommunityHealthWorkerResponse> results = service.findByStatus(status);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    void shouldSearchWithFilters() {
        // Given
        String region = "Nairobi";
        CommunityHealthWorkers.Status status = CommunityHealthWorkers.Status.AVAILABLE;
        String city = "Nairobi";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "ASC";

        Page<CommunityHealthWorkers> chwPage = new PageImpl<>(Arrays.asList(testChw));
        when(chwRepo.searchWithFilters(eq(region), eq(status), eq(city), any(Pageable.class)))
                .thenReturn(chwPage);

        // When
        Page<CommunityHealthWorkerResponse> results = service.search(region, status, city, page, size, sortBy, sortDirection);

        // Then
        assertThat(results).isNotNull();
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getRegion()).isEqualTo(region);
        assertThat(results.getContent().get(0).getFullName()).contains("Grace");
    }

    @Test
    void shouldBuildFullNameCorrectly() {
        // Given
        when(chwRepo.findById(1L)).thenReturn(Optional.of(testChw));

        // When
        CommunityHealthWorkerResponse response = service.get(1L);

        // Then
        assertThat(response.getFullName()).isEqualTo("Grace Wanjiru Akinyi");
        assertThat(response.getAvatar()).isEqualTo("GA");
    }

    @Test
    void shouldBuildFullNameWithoutMiddleName() {
        // Given
        testChw.setMiddleName(null);
        when(chwRepo.findById(1L)).thenReturn(Optional.of(testChw));

        // When
        CommunityHealthWorkerResponse response = service.get(1L);

        // Then
        assertThat(response.getFullName()).isEqualTo("Grace Akinyi");
        assertThat(response.getAvatar()).isEqualTo("GA");
    }

    @Test
    void shouldHandleNullPerformanceMetrics() {
        // Given
        testChw.setMonthlyVisits(null);
        testChw.setSuccessRate(null);
        testChw.setRating(null);
        when(chwRepo.findById(1L)).thenReturn(Optional.of(testChw));

        // When
        CommunityHealthWorkerResponse response = service.get(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMonthlyVisits()).isNull();
        assertThat(response.getSuccessRate()).isNull();
        assertThat(response.getRating()).isNull();
    }
}

