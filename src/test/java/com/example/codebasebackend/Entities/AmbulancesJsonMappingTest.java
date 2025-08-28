package com.example.codebasebackend.Entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmbulancesJsonMappingTest {

    @Test
    void mapsVehicleNumberAliasToVehiclePlate_andIgnoresUnknown() throws Exception {
        String json = "{\n" +
                "  \"vehicleNumber\": \"KDM 123A\",\n" +
                "  \"driverName\": \"John Mwangi\",\n" +
                "  \"driverPhone\": \"+254722334455\",\n" +
                "  \"status\": \"AVAILABLE\",\n" +
                "  \"latitude\": -1.2921,\n" +
                "  \"longitude\": 36.8219,\n" +
                "  \"vehicleType\": \"VAN\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Ambulances amb = mapper.readValue(json, Ambulances.class);

        assertEquals("KDM 123A", amb.getVehiclePlate());
        assertEquals("John Mwangi", amb.getDriverName());
        assertEquals(Ambulances.AmbulanceStatus.AVAILABLE, amb.getStatus());
        // Unknown fields should be ignored, core fields mapped
        assertNull(amb.getRegistrationNumber());
    }
}
