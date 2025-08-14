# AmbulanceDispatch Entity: Field-by-Field and Annotation Guide

Explains every line in `AmbulanceDispatch.java`: JPA mappings, validation, enums, indexes, relationships, and lifecycle defaults.

Source: `src/main/java/com/example/codebasebackend/Entities/AmbulanceDispatch.java`

## Overview
Represents an EMS dispatch record. Includes incident details, caller info, unit/crew identifiers, pickup/dropoff locations (address and GPS), timings/states, destination hospital, optional patient association, notes, and audit timestamps. Optimized with indexes and guarded by validation and defaults.

## Class-level annotations
- `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@EqualsAndHashCode(of = "id")`: Lombok generates boilerplate and uses primary key for equality.
- `@Entity`: Marks class as JPA entity.
- `@Table(name = "ambulance_dispatches", indexes = {...}, uniqueConstraints = {...})`:
  - Table named `ambulance_dispatches`.
  - Indexes on `incidentId`, `status`, `priority`, `requestTime`, `hospital_id`, `patient_id` for fast filtering.
  - Unique constraint on `incidentId` ensures one record per incident reference.

## Primary key
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
Database-generated `Long` id.

## Incident identifiers
```java
@NotBlank
@Column(nullable = false, length = 64, unique = true)
private String incidentId;

@Column(length = 80)
private String incidentType;
```
- `incidentId` required and unique (external or internal reference).
- `incidentType` optional categorization.

## Caller/requester details
```java
@Column(length = 120) private String callerName;
@Pattern("^[+0-9\\-() ]{6,32}$") @Column(length = 32) private String callerPhone;
@Column(columnDefinition = "text") private String callerNotes;
```
- Optional caller info; phone validated for basic international patterns.

## Unit / crew info
```java
@Column(length = 64) private String ambulanceUnitId;
@Column(length = 64) private String vehiclePlate;
@Column(length = 120) private String driverName;
@Column(length = 120) private String medicName;
```
Optional identifiers and crew names.

## Pickup location
```java
@Column(length = 150) private String pickupAddressLine1;
@Column(length = 150) private String pickupAddressLine2;
@Column(length = 80)  private String pickupCity;
@Column(length = 80)  private String pickupState;
@Column(length = 20)  private String pickupPostalCode;
@Column(length = 80)  private String pickupCountry;
@Column(precision = 9, scale = 6) private BigDecimal pickupLatitude;
@Column(precision = 9, scale = 6) private BigDecimal pickupLongitude;
```
- Address and/or GPS; all optional to accommodate varying data quality.

## Dropoff destination and address
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "hospital_id")
private Hospital hospital;
```
- Destination hospital (optional); foreign key `hospital_id`, lazy loaded.

```java
@Column(length = 150) private String dropoffAddressLine1;
@Column(length = 150) private String dropoffAddressLine2;
@Column(length = 80)  private String dropoffCity;
@Column(length = 80)  private String dropoffState;
@Column(length = 20)  private String dropoffPostalCode;
@Column(length = 80)  private String dropoffCountry;
@Column(precision = 9, scale = 6) private BigDecimal dropoffLatitude;
@Column(precision = 9, scale = 6) private BigDecimal dropoffLongitude;
```
- If no hospital or for non-hospital dropoff, free-form address/GPS can be used.

## Associated patient
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "patient_id")
private Patient patient;
```
- Optional patient link; foreign key `patient_id`, lazy loaded.

## Timings
```java
@Column(nullable = false)
private OffsetDateTime requestTime;

private OffsetDateTime dispatchTime;
private OffsetDateTime enRouteTime;
private OffsetDateTime onSceneTime;
private OffsetDateTime departSceneTime;
private OffsetDateTime arrivalAtHospitalTime;
private OffsetDateTime completionTime;
```
- `requestTime` is required (defaulted if null).
- Other timestamps represent lifecycle transitions; all optional.

## Status and priority
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 24)
private DispatchStatus status;

@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 16)
private DispatchPriority priority;
```
- Stored as strings for readability.
- Required; defaults applied on insert if null.

Enums:
```java
public enum DispatchStatus { REQUESTED, DISPATCHED, EN_ROUTE, ON_SCENE, TRANSPORTING, AT_HOSPITAL, COMPLETED, CANCELED }
public enum DispatchPriority { LOW, MEDIUM, HIGH, CRITICAL }
```

## Notes
```java
@Column(columnDefinition = "text")
private String notes;
```
- Free-form operational or clinical notes.

## Audit timestamps
```java
@CreationTimestamp @Column(nullable = false, updatable = false)
private OffsetDateTime createdAt;
@UpdateTimestamp @Column(nullable = false)
private OffsetDateTime updatedAt;
```
- Managed by Hibernate on insert/update.

## Lifecycle defaults
```java
@PrePersist
void prePersistDefaults() {
  if (status == null) status = DispatchStatus.REQUESTED;
  if (priority == null) priority = DispatchPriority.MEDIUM;
  if (requestTime == null) requestTime = OffsetDateTime.now();
}
```
- Provides safe defaults for key fields at insert time.

## Indexes and unique constraints
- Indexes: `incidentId`, `status`, `priority`, `requestTime`, `hospital_id`, `patient_id`.
- Unique: `incidentId`.

## Repository methods
In `AmbulanceDispatchRepository`:
```java
findByIncidentId(...);
findByStatus(...);
findByPriority(...);
findByHospitalId(...);
findByPatientId(...);
findByRequestTimeBetween(from, to);
```
- Common queries for dashboards, reporting, and lookups.

## Example usage
```java
repository.findByIncidentId("INC-2025-000123");
repository.findByStatus(AmbulanceDispatch.DispatchStatus.EN_ROUTE);
repository.findByRequestTimeBetween(from, to);
repository.findByHospitalId(hospitalId);
repository.findByPatientId(patientId);
```

## Future enhancements
- Add domain validations for allowed state transitions.
- Add geospatial indexing (e.g., PostGIS) for proximity queries.
- Track crew members as separate entities and relate to dispatch.
- Emit domain events when status changes.

