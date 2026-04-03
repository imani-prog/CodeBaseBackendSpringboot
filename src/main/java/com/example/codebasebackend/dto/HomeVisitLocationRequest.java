package com.example.codebasebackend.dto;

import java.math.BigDecimal;

public class HomeVisitLocationRequest {
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
}

