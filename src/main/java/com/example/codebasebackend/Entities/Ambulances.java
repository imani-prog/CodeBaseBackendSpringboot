package com.example.codebasebackend.Entities;

import jakarta.persistence.*;


import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@Entity
@jakarta.persistence.Entity
@Table(
        name = "ambulances",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ambulances_vehicle_plate", columnNames = {"vehicle_plate"}),
                @UniqueConstraint(name = "uk_ambulances_registration_number", columnNames = {"registration_number"})
        },
        indexes = {
                @Index(name = "idx_ambulances_status", columnList = "status"),
                @Index(name = "idx_ambulances_driver_name", columnList = "driver_name"),
                @Index(name = "idx_ambulances_model", columnList = "model"),
                @Index(name = "idx_ambulances_year", columnList = "year"),
                @Index(name = "idx_ambulances_gps_enabled", columnList = "gps_enabled"),
                @Index(name = "idx_ambulances_equipped_for_icu", columnList = "equipped_for_icu")
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

    @Enumerated(EnumType.STRING)
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

    @Enumerated(EnumType.STRING)
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

    public enum AmbulanceStatus {
        AVAILABLE,
        BUSY,
        MAINTENANCE
    }

    public enum FuelType {
        DIESEL,
        PETROL,
        ELECTRIC
    }
}
