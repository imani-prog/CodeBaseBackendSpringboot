package com.example.codebasebackend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class PharmacyRequest {
    private Long id; // for upsert operations

    @NotBlank
    private String name;
    private String address;
    private String city;
    private String postalCode;
    private String phone;
    private String hours;
    private String distanceText;
    private Double rating;
    private String deliveryFee;
    private String estimatedDelivery;
    private Boolean nhifAccepted;
    private Boolean offersDelivery;
    private List<String> services;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getHours() { return hours; }
    public void setHours(String hours) { this.hours = hours; }
    public String getDistanceText() { return distanceText; }
    public void setDistanceText(String distanceText) { this.distanceText = distanceText; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public String getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(String deliveryFee) { this.deliveryFee = deliveryFee; }
    public String getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    public Boolean getNhifAccepted() { return nhifAccepted; }
    public void setNhifAccepted(Boolean nhifAccepted) { this.nhifAccepted = nhifAccepted; }
    public Boolean getOffersDelivery() { return offersDelivery; }
    public void setOffersDelivery(Boolean offersDelivery) { this.offersDelivery = offersDelivery; }
    public List<String> getServices() { return services; }
    public void setServices(List<String> services) { this.services = services; }
}

