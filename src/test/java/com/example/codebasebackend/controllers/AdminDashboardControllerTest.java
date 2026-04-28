package com.example.codebasebackend.controllers;

import com.example.codebasebackend.dto.AdminDashboardResponse;
import com.example.codebasebackend.services.AdminDashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminDashboardController.class)
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminDashboardService adminDashboardService;

    @MockBean
    private DataSource dataSource;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboardReturnsPayload() throws Exception {
        AdminDashboardResponse response = AdminDashboardResponse.builder()
            .kpis(java.util.List.of(
                AdminDashboardResponse.KpiItem.builder()
                    .label("Active Patients")
                    .value("10")
                    .delta("0.0%")
                    .icon("Users")
                    .tone("text-blue-700")
                    .build()
            ))
            .build();

        when(adminDashboardService.getDashboard()).thenReturn(response);

        mockMvc.perform(get("/api/admin/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.kpis[0].label").value("Active Patients"));
    }
}

