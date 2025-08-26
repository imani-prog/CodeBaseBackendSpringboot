package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Ambulances {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vehiclePlate;

    @Column(nullable = false)
    private String driverName;

    @Column(nullable = false)
    private String status; // e.g., AVAILABLE, BUSY, MAINTENANCE

    @Column
    private String medicName;

    @Column
    private String notes;

    @Column(nullable = false, unique = true, length = 20)
    private String registrationNumber; // Unique registration number for the ambulance

    @Column(nullable = false)
    private String model; // Model of the ambulance vehicle

    @Column(nullable = false)
    private int year; // Year of manufacture

    @Column(nullable = false)
    private String fuelType; // Fuel type (e.g., Diesel, Petrol, Electric)

    @Column(nullable = false)
    private int capacity; // Maximum number of passengers or patients

    @Column(nullable = false)
    private boolean equippedForICU; // Whether the ambulance is equipped for ICU transport

    @Column(nullable = false)
    private boolean gpsEnabled; // Whether the ambulance has GPS tracking enabled

    @Column(nullable = false)
    private String insurancePolicyNumber; // Insurance policy number for the ambulance

    @Column(nullable = false)
    private String insuranceProvider; // Insurance provider name
}
