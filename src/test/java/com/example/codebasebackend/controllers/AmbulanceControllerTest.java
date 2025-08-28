package com.example.codebasebackend.controllers;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.services.AmbulanceService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AmbulanceController.class)
class AmbulanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AmbulanceService ambulanceService;

    // Mock DataSource to satisfy CommandLineRunner bean in main application during context load
    @MockBean
    private DataSource dataSource;

    @Test
    void postMapsVehicleNumberAndIgnoresUnknownFields() throws Exception {
        when(ambulanceService.addAmbulance(any(Ambulances.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // Avoid real DB call if CommandLineRunner executes
        when(dataSource.getConnection()).thenThrow(new SQLException("test"));

        String body = "{\n" +
                "  \"vehicleNumber\": \"KDM 123A\",\n" +
                "  \"vehicleType\": \"VAN\",\n" +
                "  \"driverName\": \"John Mwangi\",\n" +
                "  \"driverPhone\": \"+254722334455\",\n" +
                "  \"hospital\": { \"id\": 10 },\n" +
                "  \"status\": \"AVAILABLE\",\n" +
                "  \"latitude\": -1.2921,\n" +
                "  \"longitude\": 36.8219\n" +
                "}";

        mockMvc.perform(post("/api/ambulances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        ArgumentCaptor<Ambulances> captor = ArgumentCaptor.forClass(Ambulances.class);
        verify(ambulanceService).addAmbulance(captor.capture());
        Ambulances sent = captor.getValue();
        assertThat(sent.getVehiclePlate()).isEqualTo("KDM 123A");
        assertThat(sent.getDriverName()).isEqualTo("John Mwangi");
        assertThat(sent.getStatus()).isEqualTo(Ambulances.AmbulanceStatus.AVAILABLE);
        // Unknowns not mapped
        assertThat(sent.getRegistrationNumber()).isNull();
        assertThat(sent.getModel()).isNull();
        assertThat(sent.getFuelType()).isNull();
    }

    @Test
    void getByPlate_usesDedicatedRoute_andReturnsOk() throws Exception {
        Ambulances amb = new Ambulances();
        amb.setVehiclePlate("KDJ 778J");
        amb.setDriverName("Jane Doe");
        amb.setStatus(Ambulances.AmbulanceStatus.AVAILABLE);
        when(ambulanceService.getAmbulanceByVehiclePlate("KDJ 778J")).thenReturn(amb);
        // Avoid real DB call if CommandLineRunner executes
        when(dataSource.getConnection()).thenThrow(new SQLException("test"));

        mockMvc.perform(get("/api/ambulances/by-plate/{vehiclePlate}", "KDJ 778J"))
                .andExpect(status().isOk());

        verify(ambulanceService).getAmbulanceByVehiclePlate("KDJ 778J");
    }
}
