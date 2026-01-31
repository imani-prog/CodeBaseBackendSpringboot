package com.example.codebasebackend.Entities;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Getter
@Setter
@jakarta.persistence.Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "ambulance_tracking",
    indexes = {
        @Index(name = "idx_tracking_ambulance", columnList = "ambulance_id"),
        @Index(name = "idx_tracking_timestamp", columnList = "timestamp"),
        @Index(name = "idx_tracking_active", columnList = "is_active"),
        @Index(name = "idx_tracking_ambulance_timestamp", columnList = "ambulance_id, timestamp")
    }
)
public class AmbulanceTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ambulance_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Ambulances ambulance;

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;

    @Column
    private Integer speed;

    @Column
    private Integer heading;

    @Column(name = "altitude")
    private Integer altitude;

    @Column(name = "accuracy")
    private Integer accuracy;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(name = "signal_strength")
    private Integer signalStrength;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "location_address", length = 200)
    private String locationAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AmbulanceDispatch dispatch;

    @PrePersist
    void prePersist() {
        if (timestamp == null) timestamp = OffsetDateTime.now();
    }
}
