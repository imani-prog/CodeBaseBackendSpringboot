package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Ambulances;
import com.example.codebasebackend.Entities.Ambulances.AmbulanceStatus;
import com.example.codebasebackend.Entities.Ambulances.AmbulanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulances, Long> {

    // Existing
    Optional<Ambulances> findByVehiclePlateIgnoreCase(String vehiclePlate);

    // ==================== STATUS QUERIES ====================
    List<Ambulances> findByStatus(AmbulanceStatus status);

    List<Ambulances> findByStatusIn(List<AmbulanceStatus> statuses);

    long countByStatus(AmbulanceStatus status);

    @Query("SELECT COUNT(a) FROM Ambulances a WHERE a.status = 'AVAILABLE'")
    long countAvailable();

    @Query("SELECT COUNT(a) FROM Ambulances a WHERE a.status IN ('BUSY', 'DISPATCHED', 'EN_ROUTE', 'ON_SCENE', 'TRANSPORTING')")
    long countBusy();

    // ==================== TYPE QUERIES ====================
    List<Ambulances> findByType(AmbulanceType type);

    long countByType(AmbulanceType type);

    // ==================== GPS & TRACKING ====================
    List<Ambulances> findByGpsEnabledTrue();

    @Query("SELECT a FROM Ambulances a WHERE a.status = 'AVAILABLE' AND a.gpsEnabled = true")
    List<Ambulances> findAvailableWithGPS();

    @Query("SELECT a FROM Ambulances a WHERE a.currentLatitude IS NOT NULL AND a.currentLongitude IS NOT NULL")
    List<Ambulances> findAllWithLocation();

    // ==================== MAINTENANCE ====================
    @Query("SELECT a FROM Ambulances a WHERE a.nextMaintenanceDate <= :date AND a.status != 'MAINTENANCE'")
    List<Ambulances> findMaintenanceDue(@Param("date") LocalDate date);

    @Query("SELECT a FROM Ambulances a WHERE a.nextMaintenanceDate BETWEEN :startDate AND :endDate")
    List<Ambulances> findMaintenanceDueBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Ambulances> findByStatus(String status);

    // ==================== DRIVER QUERIES ====================
    Optional<Ambulances> findByCurrentDriverId(Long driverId);

    @Query("SELECT a FROM Ambulances a WHERE a.currentDriver IS NULL AND a.status = 'AVAILABLE'")
    List<Ambulances> findAvailableWithoutDriver();

    // ==================== EQUIPMENT QUERIES ====================
    @Query("SELECT a FROM Ambulances a WHERE a.equippedForICU = true AND a.status = 'AVAILABLE'")
    List<Ambulances> findAvailableICUEquipped();

    // ==================== STATISTICS ====================
    @Query("SELECT AVG(a.mileage) FROM Ambulances a")
    Double getAverageMileage();

    @Query("SELECT AVG(a.fuelLevel) FROM Ambulances a WHERE a.fuelLevel IS NOT NULL")
    Double getAverageFuelLevel();

    @Query("SELECT SUM(a.totalDispatches) FROM Ambulances a")
    Long getTotalDispatches();

    // ==================== SEARCH ====================
    @Query("SELECT a FROM Ambulances a WHERE " +
           "LOWER(a.vehiclePlate) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.model) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.currentLocation) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Ambulances> searchAmbulances(@Param("search") String search);
}
