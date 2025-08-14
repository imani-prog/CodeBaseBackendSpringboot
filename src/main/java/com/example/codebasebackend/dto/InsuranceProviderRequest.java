package com.example.codebasebackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class InsuranceProviderRequest {
    @NotBlank
    private String name;
    private String payerId;
    private String registrationNumber;

    @Email private String email;
    private String phone;
    private String fax;
    private String website;
    private String providerPortalUrl;
    private String claimsSubmissionUrl;
    @Email private String claimsSubmissionEmail;
    private String supportPhone;
    @Email private String supportEmail;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private String status; // enum name
    private String notes;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPayerId() { return payerId; }
    public void setPayerId(String payerId) { this.payerId = payerId; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getProviderPortalUrl() { return providerPortalUrl; }
    public void setProviderPortalUrl(String providerPortalUrl) { this.providerPortalUrl = providerPortalUrl; }
    public String getClaimsSubmissionUrl() { return claimsSubmissionUrl; }
    public void setClaimsSubmissionUrl(String claimsSubmissionUrl) { this.claimsSubmissionUrl = claimsSubmissionUrl; }
    public String getClaimsSubmissionEmail() { return claimsSubmissionEmail; }
    public void setClaimsSubmissionEmail(String claimsSubmissionEmail) { this.claimsSubmissionEmail = claimsSubmissionEmail; }
    public String getSupportPhone() { return supportPhone; }
    public void setSupportPhone(String supportPhone) { this.supportPhone = supportPhone; }
    public String getSupportEmail() { return supportEmail; }
    public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

