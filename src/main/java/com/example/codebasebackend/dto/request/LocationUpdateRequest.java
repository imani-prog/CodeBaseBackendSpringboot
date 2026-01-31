package com.example.codebasebackend.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateRequest {
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private BigDecimal latitude;
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private BigDecimal longitude;
    @Min(value = 0, message = "Speed must be >= 0")
    @Max(value = 250, message = "Speed must be <= 250")
    private Integer speed;
    @Min(value = 0, message = "Heading must be >= 0")
    @Max(value = 359, message = "Heading must be <= 359")
    private Integer heading;
    @Min(value = 0, message = "Battery level must be >= 0")
    @Max(value = 100, message = "Battery level must be <= 100")
    private Integer batteryLevel;
    @Min(value = 1, message = "Signal strength must be >= 1")
    @Max(value = 5, message = "Signal strength must be <= 5")
    private Integer signalStrength;
    private String locationAddress;
    private Long dispatchId;
}
