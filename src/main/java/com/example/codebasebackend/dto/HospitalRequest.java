package com.example.codebasebackend.dto;

import com.example.codebasebackend.Entities.Hospital;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class HospitalRequest {
    @NotBlank
    private String name;
    private Hospital.HospitalType type;
    @NotBlank
    private String registrationNumber;
    private String taxId;
    @Email
    private String email;
    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    private String mainPhone;
    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    private String altPhone;
    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid fax format")
    private String fax;
    private String website;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    private String adminContactName;
    @Email
    private String adminContactEmail;
    @Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
    private String adminContactPhone;
    @Min(0)
    private Integer numberOfBeds;
    @Min(0)
    private Integer numberOfIcuBeds;
    @Min(0)
    private Integer numberOfAmbulances;
    private String servicesOffered;
    private String departments;
    private String operatingHours;
    private String acceptedInsurance;
    private Hospital.HospitalStatus status;
    private String notes;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Hospital.HospitalType getType() { return type; }
    public void setType(Hospital.HospitalType type) { this.type = type; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMainPhone() { return mainPhone; }
    public void setMainPhone(String mainPhone) { this.mainPhone = mainPhone; }
    public String getAltPhone() { return altPhone; }
    public void setAltPhone(String altPhone) { this.altPhone = altPhone; }
    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public java.math.BigDecimal getLatitude() { return latitude; }
    public void setLatitude(java.math.BigDecimal latitude) { this.latitude = latitude; }
    public java.math.BigDecimal getLongitude() { return longitude; }
    public void setLongitude(java.math.BigDecimal longitude) { this.longitude = longitude; }
    public String getAdminContactName() { return adminContactName; }
    public void setAdminContactName(String adminContactName) { this.adminContactName = adminContactName; }
    public String getAdminContactEmail() { return adminContactEmail; }
    public void setAdminContactEmail(String adminContactEmail) { this.adminContactEmail = adminContactEmail; }
    public String getAdminContactPhone() { return adminContactPhone; }
    public void setAdminContactPhone(String adminContactPhone) { this.adminContactPhone = adminContactPhone; }
    public Integer getNumberOfBeds() { return numberOfBeds; }
    public void setNumberOfBeds(Integer numberOfBeds) { this.numberOfBeds = numberOfBeds; }
    public Integer getNumberOfIcuBeds() { return numberOfIcuBeds; }
    public void setNumberOfIcuBeds(Integer numberOfIcuBeds) { this.numberOfIcuBeds = numberOfIcuBeds; }
    public Integer getNumberOfAmbulances() { return numberOfAmbulances; }
    public void setNumberOfAmbulances(Integer numberOfAmbulances) { this.numberOfAmbulances = numberOfAmbulances; }
    public String getServicesOffered() { return servicesOffered; }
    public void setServicesOffered(String servicesOffered) { this.servicesOffered = servicesOffered; }
    public String getDepartments() { return departments; }
    public void setDepartments(String departments) { this.departments = departments; }
    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }
    public String getAcceptedInsurance() { return acceptedInsurance; }
    public void setAcceptedInsurance(String acceptedInsurance) { this.acceptedInsurance = acceptedInsurance; }
    public Hospital.HospitalStatus getStatus() { return status; }
    public void setStatus(Hospital.HospitalStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
