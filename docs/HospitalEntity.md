# Hospital Entity: Field-by-Field and Annotation Guide

This document explains every line and concept used in `Hospital.java`, covering JPA/Hibernate mappings, Bean Validation, Lombok, indexes/constraints, lifecycle defaults, and the relationship to Patient.

Source file: `src/main/java/com/example/codebasebackend/Entities/Hospital.java`

## Overview
The `Hospital` class represents a healthcare facility. It is a JPA entity with:
- A generated primary key (`id`).
- Identity and regulatory fields (name, type, registrationNumber, taxId).
- Contact and address info (email, phones, fax, website, address, geolocation).
- Administrative contact details.
- Capacity/resources (beds, ICU beds, ambulances).
- Offerings and operations (services, departments, hours, accepted insurance).
- Status and notes.
- A one-to-many relationship to `Patient`.
- Audit timestamps (`createdAt`, `updatedAt`).
- Bean Validation for input quality.
- Indexes and uniqueness to optimize lookups and enforce data integrity.

## Imports
- `jakarta.persistence.*`: JPA annotations like `@Entity`, `@Table`, `@Id`, `@Column`, `@OneToMany`, etc.
- `lombok.*`: Reduces boilerplate (getters, setters, builder, constructors, equals/hashCode).
- `org.hibernate.annotations.*`: Hibernate timestamps for created/updated fields.
- `java.math.BigDecimal`: For latitude/longitude with precision/scale.
- `java.time.OffsetDateTime`: For audit timestamps with offset.
- `java.util.*`: For the `Set<Patient>` collection.
- `jakarta.validation.constraints.*`: Bean Validation (e.g., `@NotBlank`, `@Email`, `@Pattern`, `@Min`).

## Class-level annotations
- `@Getter`, `@Setter`: Lombok generates accessors.
- `@Builder`: Lombok builder pattern for constructing instances.
- `@NoArgsConstructor`, `@AllArgsConstructor`: Lombok constructors.
- `@EqualsAndHashCode(of = "id")`: Equality based on primary key only (safest for JPA entities).
- `@jakarta.persistence.Entity`: Marks class as a JPA entity.
- `@Table(name = "hospitals", indexes = {...}, uniqueConstraints = {...})`:
  - `name = "hospitals"`: Table name.
  - Indexes speed common queries: `name`, `city`, `email`, `registrationNumber`.
  - `uniqueConstraints`: `registrationNumber` must be unique.

## Primary key
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
- Database-generated long ID (PostgreSQL typically uses `bigserial`).

## Identity and regulatory
```java
@NotBlank
@Column(nullable = false, length = 150)
private String name;
```
- Required hospital name with max length.

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 24)
private HospitalType type;
```
- Required hospital type enum stored as a string (e.g., `GENERAL`).

```java
@NotBlank
@Column(nullable = false, length = 64, unique = true)
private String registrationNumber;
```
- Required and unique regulatory or licensing ID.

```java
@Column(length = 64)
private String taxId;
```
- Optional tax identifier.

## Contact
```java
@Email
@Column(length = 150)
private String email;
```
- Optional email validated syntactically.

```java
@Pattern(regexp = "^[+0-9\\-() ]{6,32}$")
@Column(length = 32)
private String mainPhone;

@Pattern(regexp = "^[+0-9\\-() ]{6,32}$")
@Column(length = 32)
private String altPhone;

@Pattern(regexp = "^[+0-9\\-() ]{6,32}$")
@Column(length = 32)
private String fax;
```
- Optional phone/fax fields validated for a simple international format.

```java
@Column(length = 200)
private String website;
```
- Optional website URL as a simple string.

## Address and geolocation
```java
@Column(length = 150) private String addressLine1;
@Column(length = 150) private String addressLine2;
@Column(length = 80)  private String city;
@Column(length = 80)  private String state;
@Column(length = 20)  private String postalCode;
@Column(length = 80)  private String country;
```
- Optional address lines with safe lengths.

```java
@Column(precision = 9, scale = 6)
private BigDecimal latitude;

@Column(precision = 9, scale = 6)
private BigDecimal longitude;
```
- Optional GPS coordinates; precision/scale store degrees with micro-degree resolution.

## Administrative contact
```java
@Column(length = 120) private String adminContactName;
@Email @Column(length = 150) private String adminContactEmail;
@Pattern(regexp = "^[+0-9\\-() ]{6,32}$") @Column(length = 32) private String adminContactPhone;
```
- Optional designated contact for administrative matters.

## Capacity and resources
```java
@Min(0) private Integer numberOfBeds;
@Min(0) private Integer numberOfIcuBeds;
@Min(0) private Integer numberOfAmbulances;
```
- Non-negative counts of available resources.

## Offerings and operations
```java
@Column(columnDefinition = "text") private String servicesOffered;
@Column(columnDefinition = "text") private String departments;
@Column(columnDefinition = "text") private String operatingHours;
@Column(columnDefinition = "text") private String acceptedInsurance;
```
- Free-form or JSON-like text fields for operational/clinical offerings and hours.

## Status and notes
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 24)
private HospitalStatus status;

@Column(columnDefinition = "text")
private String notes;
```
- Required operational status, optional notes for internal comments.

## Relationship to Patient
```java
@Builder.Default
@OneToMany(mappedBy = "hospital", fetch = FetchType.LAZY)
private Set<Patient> patients = new HashSet<>();
```
- A hospital has many patients; the relationship is owned by `Patient.hospital` (`mappedBy`).
- `LAZY` prevents loading patients unless requested.
- `@Builder.Default` ensures Lombok builder initializes the set to an empty `HashSet`.

## Audit timestamps
```java
@CreationTimestamp
@Column(nullable = false, updatable = false)
private OffsetDateTime createdAt;

@UpdateTimestamp
@Column(nullable = false)
private OffsetDateTime updatedAt;
```
- Automatically managed by Hibernate on insert/update.

## Lifecycle defaults
```java
@PrePersist
void prePersistDefaults() {
    if (status == null) status = HospitalStatus.ACTIVE;
    if (type == null) type = HospitalType.GENERAL;
}
```
- Ensures sensible defaults when fields are not provided.

## Enums
```java
public enum HospitalType { GENERAL, CLINIC, SPECIALTY, TEACHING, REHABILITATION, EMERGENCY_CENTER }
public enum HospitalStatus { ACTIVE, INACTIVE, UNDER_MAINTENANCE, CLOSED }
```
- Stored as strings for readability and schema stability.

## Indexes and uniqueness
- Indexes on `name`, `city`, `email`, and `registrationNumber` improve query performance.
- Unique constraint on `registrationNumber` prevents duplicates.

## Persistence lifecycle summary
1. Create a `Hospital` instance (constructor or builder).
2. Save via Spring Data JPA; `@PrePersist` sets defaults; Hibernate fills timestamps.
3. On updates, `updatedAt` is refreshed automatically.

## Example usage (repository)
```java
hospitalRepository.findByRegistrationNumber("REG-123");
hospitalRepository.findByNameContainingIgnoreCase("central");
hospitalRepository.findByCityIgnoreCase("nairobi");
hospitalRepository.findByStatus(Hospital.HospitalStatus.ACTIVE);
```

## Future extensions
- Add `@OneToMany` for departments, services, and operating hours as structured entities if needed.
- Introduce geospatial indexing or PostGIS for location-based queries.
- Add auditing with user info for creation/modification.
- Manage relationships with `Appointment`, `AmbulanceDispatch`, etc., when modeled.

