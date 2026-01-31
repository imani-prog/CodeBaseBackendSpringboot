package com.example.codebasebackend.Entities;

import jakarta.persistence.*;


import lombok.*;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@jakarta.persistence.Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "ambulances",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ambulances_vehicle_plate", columnNames = {"vehicle_plate"}),
                @UniqueConstraint(name = "uk_ambulances_registration_number", columnNames = {"registration_number"})
        },
        indexes = {
                @Index(name = "idx_ambulances_status", columnList = "status"),
                @Index(name = "idx_ambulances_type", columnList = "ambulance_type"),
                @Index(name = "idx_ambulances_driver_name", columnList = "driver_name"),
                @Index(name = "idx_ambulances_model", columnList = "model"),
                @Index(name = "idx_ambulances_year", columnList = "year"),
                @Index(name = "idx_ambulances_gps_enabled", columnList = "gps_enabled"),
                @Index(name = "idx_ambulances_equipped_for_icu", columnList = "equipped_for_icu"),
                @Index(name = "idx_ambulances_vehicle_plate", columnList = "vehicle_plate"),
                @Index(name = "idx_ambulances_current_location", columnList = "current_location"),
                @Index(name = "idx_ambulances_last_dispatch", columnList = "last_dispatch_time"),
                @Index(name = "idx_ambulances_next_maintenance", columnList = "next_maintenance_date"),
                @Index(name = "idx_ambulances_mileage", columnList = "mileage")
        }
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ambulances {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_plate", nullable = false)
    @JsonAlias({"vehicleNumber"})
    private String vehiclePlate;

    @Column(name = "driver_name", nullable = false)
    private String driverName;

    @Column(name = "driver_phone", nullable = false, length = 32)
    private String driverPhone;

    @Convert(converter = AmbulanceStatusConverter.class)
    @Column(name = "status", nullable = false, length = 24)
    private AmbulanceStatus status; // e.g., AVAILABLE, BUSY, MAINTENANCE

    @Column(name = "medic_name")
    private String medicName;

    @Column
    private String notes;

    @Column(name = "registration_number", length = 20)
    private String registrationNumber; // Unique registration number for the ambulance

    @Column
    private String model; // Model of the ambulance vehicle

    @Column(nullable = false)
    private int year; // Year of manufacture

    @Convert(converter = FuelTypeConverter.class)
    @Column(name = "fuel_type", length = 16)
    private FuelType fuelType; // Fuel type (e.g., Diesel, Petrol, Electric)

    @Column(nullable = false)
    private int capacity; // Maximum number of passengers or patients

    @Column(name = "equipped_for_icu", nullable = false)
    private boolean equippedForICU;

    @Column(name = "gps_enabled", nullable = false)
    private boolean gpsEnabled; // Whether the ambulance has GPS tracking enabled

    @Column(name = "insurance_policy_number")
    private String insurancePolicyNumber; // Insurance policy number for the ambulance

    @Column(name = "insurance_provider")
    private String insuranceProvider; // Insurance provider name

    // ==================== DRIVER RELATIONSHIP ====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_driver_id")
    private AmbulanceDriver currentDriver;

    // ==================== AMBULANCE TYPE ====================
    @Convert(converter = AmbulanceTypeConverter.class)
    @Column(name = "ambulance_type", nullable = false, length = 32)
    private AmbulanceType type;

    // ==================== LOCATION & TRACKING ====================
    @Column(name = "current_location", length = 200)
    private String currentLocation;

    @Column(name = "current_latitude", precision = 9, scale = 6)
    private BigDecimal currentLatitude;

    @Column(name = "current_longitude", precision = 9, scale = 6)
    private BigDecimal currentLongitude;

    // ==================== MAINTENANCE ====================
    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    @Column(name = "last_maintenance_mileage")
    private Integer lastMaintenanceMileage;

    // ==================== OPERATIONAL METRICS ====================
    @Column(nullable = false)
    @Builder.Default
    private Integer mileage = 0;

    @Column(name = "fuel_level")
    private Integer fuelLevel; // Percentage 0-100

    @Column(name = "last_dispatch_time")
    private OffsetDateTime lastDispatchTime;

    @Column(name = "total_dispatches")
    @Builder.Default
    private Integer totalDispatches = 0;

    @Column(name = "average_response_minutes")
    private Integer averageResponseMinutes;

    // ==================== EQUIPMENT ====================
    @Column(name = "equipment", columnDefinition = "text")
    private String equipmentJson; // Store as JSON array: ["Defibrillator", "Oxygen Tank", ...]

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ambulance_equipment_mapping",
        joinColumns = @JoinColumn(name = "ambulance_id"),
        inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    @Builder.Default
    private Set<AmbulanceEquipment> equipment = new HashSet<>();

    // ==================== MEDIA ====================
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    // ==================== AUDIT TIMESTAMPS ====================
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // ==================== HELPER METHODS ====================
    @Transient
    public List<String> getEquipmentList() {
        if (equipmentJson == null || equipmentJson.isEmpty()) return new ArrayList<>();
        try {
            return new ObjectMapper().readValue(equipmentJson, new TypeReference<List<String>>(){});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public void setEquipmentList(List<String> equipment) {
        try {
            this.equipmentJson = new ObjectMapper().writeValueAsString(equipment);
        } catch (JsonProcessingException e) {
            this.equipmentJson = null;
        }
    }

    // ==================== ENUMS ====================
    public enum AmbulanceType {
        BASIC_LIFE_SUPPORT,
        ADVANCED_LIFE_SUPPORT,
        CRITICAL_CARE,
        PATIENT_TRANSPORT,
        NEONATAL,
        BARIATRIC;

        public static AmbulanceType fromString(String value) {
            if (value == null) return null;
            String normalized = value.trim().replaceAll("\\s+", "_").toUpperCase();
            for (AmbulanceType t : values()) {
                if (t.name().equalsIgnoreCase(normalized)) return t;
            }
            throw new IllegalArgumentException("Unknown AmbulanceType: " + value);
        }
    }

    public enum AmbulanceStatus {
        AVAILABLE,      // Ready for dispatch
        DISPATCHED,     // Assigned to a call
        EN_ROUTE,       // Traveling to incident scene
        ON_SCENE,       // At incident location
        TRANSPORTING,   // Patient onboard, en route to hospital
        AT_HOSPITAL,    // Delivering patient to hospital
        RETURNING,      // Returning to base
        BUSY,           // Generic busy state
        MAINTENANCE,    // Under maintenance/repair
        OUT_OF_SERVICE, // Temporarily offline
        ON_CALL;        // On active emergency call

        public static AmbulanceStatus fromString(String value) {
            if (value == null) return null;
            String normalized = value.trim().replaceAll("\\s+", "_").toUpperCase();
            for (AmbulanceStatus s : values()) {
                if (s.name().equalsIgnoreCase(normalized) || s.name().equals(normalized)) return s;
            }
            throw new IllegalArgumentException("Unknown AmbulanceStatus: " + value);
        }
    }

    public enum FuelType {
        DIESEL,
        PETROL,
        ELECTRIC;

        public static FuelType fromString(String value) {
            if (value == null) return null;
            String normalized = value.trim().toUpperCase();
            for (FuelType f : values()) {
                if (f.name().equalsIgnoreCase(normalized) || f.name().equals(normalized)) return f;
            }
            throw new IllegalArgumentException("Unknown FuelType: " + value);
        }
    }

    @Converter(autoApply = false)
    public static class FuelTypeConverter implements AttributeConverter<FuelType, String> {
        @Override
        public String convertToDatabaseColumn(FuelType attribute) {
            return attribute == null ? null : attribute.name();
        }

        @Override
        public FuelType convertToEntityAttribute(String dbData) {
            if (dbData == null) return null;
            try {
                return FuelType.valueOf(dbData.toUpperCase());
            } catch (IllegalArgumentException ex) {
                return FuelType.fromString(dbData);
            }
        }
    }

    @Converter(autoApply = false)
    public static class AmbulanceStatusConverter implements AttributeConverter<AmbulanceStatus, String> {
        @Override
        public String convertToDatabaseColumn(AmbulanceStatus attribute) {
            return attribute == null ? null : attribute.name();
        }

        @Override
        public AmbulanceStatus convertToEntityAttribute(String dbData) {
            if (dbData == null) return null;
            try {
                return AmbulanceStatus.valueOf(dbData.toUpperCase());
            } catch (IllegalArgumentException ex) {
                return AmbulanceStatus.fromString(dbData);
            }
        }
    }

    @Converter(autoApply = false)
    public static class AmbulanceTypeConverter implements AttributeConverter<AmbulanceType, String> {
        @Override
        public String convertToDatabaseColumn(AmbulanceType attribute) {
            return attribute == null ? null : attribute.name();
        }

        @Override
        public AmbulanceType convertToEntityAttribute(String dbData) {
            if (dbData == null) return null;
            try {
                return AmbulanceType.valueOf(dbData.toUpperCase());
            } catch (IllegalArgumentException ex) {
                return AmbulanceType.fromString(dbData);
            }
        }
    }
}
