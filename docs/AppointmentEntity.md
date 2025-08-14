# Appointment Entity: Field-by-Field and API Guide

Explains `Appointment.java` fields/annotations and the REST endpoints wired for CRUD and queries.

Source: `src/main/java/com/example/codebasebackend/Entities/Appointment.java`

## Model overview
- Identity: `id` (PK), optional `appointmentCode` (unique human/external code).
- Relationships: `patient` (required), `hospital` (optional).
- Scheduling: `scheduledStart` (>= now), `scheduledEnd` (> start), `checkInTime`, `checkOutTime`.
- Classification: `status` (enum), `type` (enum).
- Operational: `providerName`, `room`, `location`, `reason`, `notes`, `reminderSent`.
- Audit: `createdAt`, `updatedAt`.
- Indexes: `status`, `scheduledStart`, `patient_id`, `hospital_id`.
- Unique: `appointmentCode`.

## Key annotations and behavior
- `@Entity`/`@Table` define table `appointments` with indexes/unique constraints.
- `@ManyToOne(optional=false)` to `Patient` enforces presence.
- `@FutureOrPresent` and `@Future` validate time windows.
- `@PrePersist` sets default `status=SCHEDULED` and guards invalid end before start.
- Enums stored as `STRING` for readability.

Enums
- `AppointmentStatus`: SCHEDULED, CHECKED_IN, IN_PROGRESS, COMPLETED, CANCELED, NO_SHOW, RESCHEDULED
- `AppointmentType`: CONSULTATION, FOLLOW_UP, SURGERY, LAB_TEST, IMAGING, VACCINATION, TELEHEALTH, OTHER

## Repository
- `AppointmentRepository` with finders:
  - `findByPatientIdOrderByScheduledStartAsc(...)`
  - `findByHospitalIdOrderByScheduledStartAsc(...)`
  - `findByStatusOrderByScheduledStartAsc(...)`
  - `findByScheduledStartBetweenOrderByScheduledStartAsc(from, to)`

## Service and DTOs
- `AppointmentService` + `AppointmentServiceImplementation` handle CRUD and queries.
- DTOs: `AppointmentRequest`, `AppointmentResponse` (maps enums as names/strings).
- Validates time ranges and IDs; generates a code if not provided (prefix `APT-`).

## REST endpoints
Base path: `/api/appointments`
- POST `/` create
- GET `/{id}` get by id
- PUT `/{id}` update
- DELETE `/{id}` delete
- GET `/patient/{patientId}` list by patient
- GET `/hospital/{hospitalId}` list by hospital
- GET `/status/{status}` list by status (enum name)
- GET `/range?from=...&to=...` list by scheduledStart window (ISO 8601)

Notes
- Validation errors return 400; missing entities return 404.
- `status`/`type` accepted as enum names (case-insensitive via service mapping).
- With `spring.jpa.hibernate.ddl-auto=update`, the `appointments` table and indexes are auto-managed.

