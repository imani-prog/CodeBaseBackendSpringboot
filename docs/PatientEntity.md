# Patient Entity: Field-by-Field and Annotation Guide

This document explains every line and concept used in `Patient.java`, covering JPA/Hibernate mappings, Bean Validation, Lombok, indexes/constraints, lifecycle hooks, and enums.

Source file: `src/main/java/com/example/codebasebackend/Entities/Patient.java`

## Overview
The `Patient` class represents a patient record in the database. It is a JPA entity with:
- A generated primary key (`id`).
- Demographic data (names, gender, date of birth).
- Contact and address info.
- Government/insurance identifiers.
- Emergency contact details.
- Clinical profile (allergies, medications, chronic conditions, blood type).
- Preferences/status and notes.
- Audit timestamps (`createdAt`, `updatedAt`).
- Enums stored as strings for readability.
- Database indexes and uniqueness constraints on key fields.
- Bean Validation annotations to enforce data quality.

Hibernate (via Spring Boot) auto-creates/updates the `patients` table due to `spring.jpa.hibernate.ddl-auto=update` in `application.properties`.

## Imports
- `jakarta.persistence.*`: JPA annotations like `@Entity`, `@Table`, `@Id`, `@Column`, etc.
- `lombok.*`: Generates getters/setters/builders/constructors/equality to reduce boilerplate.
- `org.hibernate.annotations.*`: Hibernate-specific features for timestamps.
- `java.time.*`: Types for date and timestamp fields.
- `jakarta.validation.constraints.*`: Bean Validation annotations for field-level constraints.

## Class-level annotations
- `@Getter` / `@Setter`: Lombok generates getters and setters for all fields at compile time.
- `@Builder`: Lombok provides a fluent builder API (`Patient.builder()...build()`).
- `@NoArgsConstructor` / `@AllArgsConstructor`: Lombok generates constructors with 0 args and all args.
- `@EqualsAndHashCode(of = "id")`: Lombok uses only `id` to implement `equals`/`hashCode` (safe for JPA entities).
- `@jakarta.persistence.Entity`: Marks the class as a JPA entity so Hibernate manages it.
- `@Table(name = "patients", indexes = {...}, uniqueConstraints = {...})`:
  - `name = "patients"`: Sets the table name to `patients`.
  - `indexes`: Database indexes to speed queries:
    - `idx_patient_last_name` on `lastName`.
    - `idx_patient_email` on `email`.
    - `idx_patient_national_id` on `nationalId`.
  - `uniqueConstraints`: Enforces uniqueness at the database level:
    - `uk_patient_email` on `email`.
    - `uk_patient_national_id` on `nationalId`.

Note: Because ddl-auto is `update`, Hibernate will attempt to create these indexes/constraints if they don’t exist. In production, prefer a migration tool (Flyway/Liquibase).

## Primary Key
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
- `@Id`: Marks `id` as the primary key.
- `@GeneratedValue(strategy = IDENTITY)`: Database generates the key (PostgreSQL typically uses `bigserial`).
- Type `Long`: Standard for JPA IDs.

## Identity fields
```java
@NotBlank
@Column(nullable = false, length = 100)
private String firstName;

@Column(length = 100)
private String middleName;

@NotBlank
@Column(nullable = false, length = 100)
private String lastName;
```
- `@NotBlank`: Bean Validation—must be non-null and not just whitespace.
- `@Column(nullable = false)`: DB-level NOT NULL.
- `length = 100`: Limits column size to 100 characters.
- `middleName` is optional (no validation, nullable column).

## Gender enum
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 16)
private Gender gender;
```
- `@Enumerated(EnumType.STRING)`: Stores the enum name (e.g., `MALE`) in the DB (human-readable, stable).
- `nullable = false`: Must be provided or will be defaulted (see `@PrePersist`).
- `length = 16`: Allocates up to 16 chars, adequate for the enum names.

## Date of birth
```java
@Past(message = "dateOfBirth must be in the past")
@Column
private LocalDate dateOfBirth;
```
- `@Past`: Bean Validation—if provided, it must be a date strictly in the past.
- `LocalDate`: Stores a date without time.
- Nullable (no `nullable = false`).

## Contact
```java
@Email
@Column(length = 150, unique = true)
private String email;
```
- `@Email`: Bean Validation—must be a syntactically valid email if present.
- `unique = true`: DB-level unique constraint (also reinforced at the table level), ensures no duplicates.

```java
@Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
@Column(length = 32)
private String phone;

@Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
@Column(length = 32)
private String secondaryPhone;
```
- `@Pattern(...)`: Allows digits, plus, dash, parentheses, and spaces, with length 6–32.
- Both phones are optional.

## Address
```java
@Column(length = 150) private String addressLine1;
@Column(length = 150) private String addressLine2;
@Column(length = 80)  private String city;
@Column(length = 80)  private String state;
@Column(length = 20)  private String postalCode;
@Column(length = 80)  private String country;
```
- All optional with sensible length limits.

## Government/insurance
```java
@Column(length = 64, unique = true)
private String nationalId;

@Column(length = 64)
private String insuranceMemberId;

@Column(length = 120)
private String insuranceProviderName;
```
- `nationalId` is unique if present (also indexed). Useful for secure lookups.
- Insurance fields are optional and stored as simple strings for now.

## Emergency contact
```java
@Column(length = 120)
private String emergencyContactName;

@Column(length = 80)
private String emergencyContactRelation;

@Pattern(regexp = "^[+0-9\\-() ]{6,32}$", message = "Invalid phone format")
@Column(length = 32)
private String emergencyContactPhone;
```
- Optional name and relation fields.
- Phone validated with the same pattern as the patient’s phone.

## Clinical profile
```java
@Column(columnDefinition = "text")
private String allergies;

@Column(columnDefinition = "text")
private String medications;

@Column(columnDefinition = "text")
private String chronicConditions;
```
- `columnDefinition = "text"`: Uses PostgreSQL `text` type for long, free-form notes.

```java
@Enumerated(EnumType.STRING)
@Column(length = 8)
private BloodType bloodType;
```
- Stores blood type enum as a short string (e.g., `A_POS`, `O_NEG`).

## Preferences and status
```java
@Column(length = 20)
private String preferredLanguage;

@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 16)
private PatientStatus status;

@Enumerated(EnumType.STRING)
@Column(length = 16)
private MaritalStatus maritalStatus;

private Boolean consentToShareData;
private Boolean smsOptIn;
private Boolean emailOptIn;

@Column(columnDefinition = "text")
private String notes;
```
- Optional `preferredLanguage` and `maritalStatus`.
- `status` is required; defaults to `ACTIVE` if not set (see `@PrePersist`).
- Consent/opt-in flags are optional booleans.
- `notes` is long-form text for free comments.

## Audit timestamps
```java
@CreationTimestamp
@Column(nullable = false, updatable = false)
private OffsetDateTime createdAt;

@UpdateTimestamp
@Column(nullable = false)
private OffsetDateTime updatedAt;
```
- `@CreationTimestamp`: Hibernate fills this on insert.
- `@UpdateTimestamp`: Hibernate updates this on every update.
- `OffsetDateTime`: Stores date-time with offset.
- `updatable = false` ensures `createdAt` isn’t changed on updates.

## Entity lifecycle defaults
```java
@PrePersist
void prePersistDefaults() {
    if (status == null) status = PatientStatus.ACTIVE;
    if (gender == null) gender = Gender.UNKNOWN;
}
```
- `@PrePersist`: Callback that runs just before the entity is inserted.
- Ensures sensible defaults:
  - `status` defaults to `ACTIVE`.
  - `gender` defaults to `UNKNOWN`.

## Relationship to Hospital
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "hospital_id")
private Hospital hospital;
```
- Each patient optionally belongs to a single hospital.
- The foreign key column is `hospital_id` and is indexed for faster lookups.
- `LAZY` loading avoids fetching hospital details unless accessed.
- This is the inverse side of `Hospital.patients` (`@OneToMany(mappedBy = "hospital")`).

## Enums
```java
public enum Gender { MALE, FEMALE, OTHER, UNKNOWN }
public enum BloodType { A_POS, A_NEG, B_POS, B_NEG, AB_POS, AB_NEG, O_POS, O_NEG }
public enum PatientStatus { ACTIVE, INACTIVE, DECEASED }
public enum MaritalStatus { SINGLE, MARRIED, DIVORCED, WIDOWED, SEPARATED, OTHER }
```
- Kept inside `Patient` to keep usage localized and avoid extra files.
- Stored as strings for readability and migration stability.

## Validation behavior
- Bean Validation annotations (e.g., `@NotBlank`, `@Email`, `@Past`, `@Pattern`) require Spring to trigger validation, typically via:
  - `@Valid` on Controller method parameters (DTOs/entities).
  - `@Validated` on Service classes or config.
- Validation failures raise `MethodArgumentNotValidException` or `ConstraintViolationException` which Spring translates to 400 responses by default (when used on request bodies).

## Indexes and uniqueness
- Indexes on `lastName`, `email`, `nationalId` help with common lookups.
- Unique constraints on `email` and `nationalId` prevent duplicates.
- Note: `@Column(unique = true)` and `@Table(uniqueConstraints = ...)` are redundant; having both is defensive. The table-level unique constraints are named explicitly for clarity.

## Persistence lifecycle summary
1. You create a `Patient` instance (manually or via builder/DTO mapping).
2. Spring Data JPA `save()` inserts it. Before insert:
   - `@PrePersist` sets defaults for `status` and `gender` if null.
   - Hibernate sets `createdAt` and `updatedAt`.
3. On update, `updatedAt` refreshes automatically.

## Example usage (repository)
```java
patientRepository.findByEmail("alice@example.com");
patientRepository.findByNationalId("ID-12345");
patientRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase("Alice", "Smith");
```

## Extending the model later
- Relationships: Add links to `Appointment`, `HealthRecord`, `Billing` with `@OneToMany` or `@ManyToOne` when those entities are defined.
- Auditing: Consider Spring Data JPA auditing (`@CreatedDate`, `@LastModifiedDate`) if you prefer framework-managed auditing.
- Internationalization: Replace `preferredLanguage` string with a constrained enum or ISO code validation.
- Migrations: Replace `ddl-auto=update` with Flyway/Liquibase for controlled schema evolution.

## Quick checklist of fields
- id
- firstName, middleName, lastName
- gender, dateOfBirth
- email, phone, secondaryPhone
- addressLine1, addressLine2, city, state, postalCode, country
- nationalId, insuranceMemberId, insuranceProviderName
- emergencyContactName, emergencyContactRelation, emergencyContactPhone
- allergies, medications, chronicConditions, bloodType
- preferredLanguage, status, maritalStatus, consentToShareData, smsOptIn, emailOptIn, notes
- createdAt, updatedAt

---
This entity is designed to be practical and forward-compatible. If you want a slimmer or stricter version, we can tailor fields, constraints, and indexes to your exact needs.
