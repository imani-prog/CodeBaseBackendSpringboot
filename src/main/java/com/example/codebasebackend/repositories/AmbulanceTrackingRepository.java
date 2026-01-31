package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.AmbulanceTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AmbulanceTrackingRepository extends JpaRepository<AmbulanceTracking, Long> {

    // Get latest tracking for an ambulance (returns most recent by timestamp)
    @Query("SELECT t FROM AmbulanceTracking t WHERE t.ambulance.id = :ambulanceId " +
           "ORDER BY t.timestamp DESC")
    List<AmbulanceTracking> findByAmbulanceIdOrderByTimestampDesc(@Param("ambulanceId") Long ambulanceId);

    // Helper method to get just the latest one
    default Optional<AmbulanceTracking> findLatestByAmbulanceId(Long ambulanceId) {
        List<AmbulanceTracking> results = findByAmbulanceIdOrderByTimestampDesc(ambulanceId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // Get tracking history for an ambulance
    List<AmbulanceTracking> findByAmbulanceIdAndTimestampBetweenOrderByTimestampDesc(
        Long ambulanceId,
        OffsetDateTime from,
        OffsetDateTime to
    );

    // Get all active tracking points
    @Query("SELECT t FROM AmbulanceTracking t WHERE t.isActive = true " +
           "AND t.timestamp >= :since ORDER BY t.ambulance.id, t.timestamp DESC")
    List<AmbulanceTracking> findAllActiveTracking(@Param("since") OffsetDateTime since);

    // Get route for a dispatch
    List<AmbulanceTracking> findByDispatchIdOrderByTimestampAsc(Long dispatchId);

    // Deactivate old tracking points
    @Modifying
    @Query("UPDATE AmbulanceTracking t SET t.isActive = false " +
           "WHERE t.ambulance.id = :ambulanceId AND t.timestamp < :timestamp")
    int deactivateOldTracking(@Param("ambulanceId") Long ambulanceId,
                               @Param("timestamp") OffsetDateTime timestamp);
}
