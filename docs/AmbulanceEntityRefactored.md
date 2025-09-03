Project changes — Ambulances entity, enums, converters, indexes, repository, service and controller

Summary

This document explains the recent code changes made to the Ambulances model and related layers so that vehicle_plate can be used as a reliable, indexed lookup key, enums are robust when reading legacy/mixed-case DB values, and the API supports lookups and updates by vehicle plate.

Files changed or added

- src/main/java/com/example/codebasebackend/Entities/Ambulances.java
  - Added unique constraints and indexes via @Table (vehicle_plate is indexed and unique).
  - Introduced public enums AmbulanceStatus and FuelType.
  - Added AttributeConverters (AmbulanceStatusConverter and FuelTypeConverter) so enum mapping is tolerant to case and formatting in the database.
  - Added @JsonAlias for vehicleNumber -> vehiclePlate so incoming JSON with vehicleNumber maps correctly.
  - driverPhone is stored as String (keeps phone formatting and + prefix).

- src/main/java/com/example/codebasebackend/repositories/AmbulanceRepository.java
  - Repository already contains Optional<Ambulances> findByVehiclePlateIgnoreCase(String vehiclePlate) — use this for faster DB lookup instead of scanning all ambulances.

- src/main/java/com/example/codebasebackend/services/AmbulanceService.java
  - Service interface supports getAmbulanceByVehiclePlate and updateAmbulanceByVehiclePlate.

- src/main/java/com/example/codebasebackend/services/AmbulanceServiceImplementation.java
  - Implementation uses the repository findByVehiclePlateIgnoreCase method and throws 404 when not found.
  - updateAmbulanceByVehiclePlate updates fields on the existing entity and saves.

- src/main/java/com/example/codebasebackend/controllers/AmbulanceController.java
  - Added endpoints:
    - GET  /api/ambulances/by-plate/{vehiclePlate}  -> getAmbulanceByVehiclePlate
    - PUT  /api/ambulances/by-plate/{vehiclePlate}  -> updateAmbulanceByVehiclePlate
  - Other standard CRUD endpoints remain unchanged.

Why these changes were necessary

- Index on vehicle_plate: vehicle_plate is frequently used to look up ambulance details. Creating an index speeds up queries that search by vehicle_plate.
- Unique constraint on vehicle_plate and registration_number: ensures uniqueness at the database level and prevents accidental duplicates.
- Enum mapping errors: the application previously threw exceptions when DB contained strings like "Diesel" that did not exactly match the enum constant name in uppercase. AttributeConverters make mapping tolerant (case-insensitive), avoiding runtime failures when reading existing data.

Enum behavior and tolerant mapping

- AmbulanceStatus enum values: AVAILABLE, BUSY, MAINTENANCE
- FuelType enum values: DIESEL, PETROL, ELECTRIC
- Converters convert enum -> database as attribute.name() (uppercase). When reading from the database, the converters try valueOf(dbData.toUpperCase()) first, and if that fails they attempt a tolerant fromString mapping which handles minor formatting differences.

Repository and service usage

- Use ambulanceRepository.findByVehiclePlateIgnoreCase(vehiclePlate) for direct DB lookup. That method uses a case-insensitive search and benefits from the vehicle_plate index.
- Service methods:
  - getAmbulanceByVehiclePlate(String vehiclePlate): returns Ambulances or throws 404.
  - updateAmbulanceByVehiclePlate(String vehiclePlate, Ambulances ambulance): finds the ambulance, maps updates, and saves.

JSON mapping and example payloads

- The JSON property vehicleNumber maps to the vehiclePlate field in the entity. You can POST either vehiclePlate or vehicleNumber.
- driverPhone should be supplied as a string to preserve formats such as "+254722334455".

Example JSON to create an ambulance (required fields included):

{
  "vehicleNumber": "KDJ 778J",
  "driverName": "Joseph Ndungu",
  "driverPhone": "+254700112233",
  "status": "AVAILABLE",
  "year": 2021,
  "fuelType": "DIESEL",
  "capacity": 7,
  "equippedForICU": false,
  "gpsEnabled": true
}

Notes:
- fuelType and status are enum-backed. Use the canonical values (DIESEL, PETROL, ELECTRIC; AVAILABLE, BUSY, MAINTENANCE) — casing is tolerant on read, but sending uppercase is safest.
- vehicle_plate is indexed and unique. When constructing queries by plate, prefer the dedicated repository method.
- When calling the endpoint using a URL with spaces, ensure proper URL encoding (e.g., "KDJ 778J" -> "KDJ%20778J").

Example curl commands

- Create ambulance:
  curl -X POST http://localhost:8080/api/ambulances \
    -H "Content-Type: application/json" \
    -d '<JSON payload above>'

- Get ambulance by plate (encode space):
  curl http://localhost:8080/api/ambulances/by-plate/KDJ%20778J

- Update ambulance by plate:
  curl -X PUT http://localhost:8080/api/ambulances/by-plate/KDJ%20778J \
    -H "Content-Type: application/json" \
    -d '<updated JSON payload>'

Testing notes

- Tests that construct Ambulances objects in code must set enum fields using the enum types (Ambulances.AmbulanceStatus.AVAILABLE etc.). That fixes previous compile errors where tests used plain strings instead of enum values.
- Existing controller unit tests demonstrate mapping from vehicleNumber and that the GET by-plate route is used.

Database migration and production concerns

- If your DB already contains ambulance rows with non-uppercase or human-friendly values (e.g., "Diesel"), the converters will handle reads. For consistency, consider a migration to normalize fuel_type and status column values to uppercase canonical names.
- The unique constraints will be enforced by the DB; if duplicates exist, migration must deduplicate before applying the constraint.

Suggested next steps

- Add validation annotations (@NotNull, @Size, @Pattern) to the entity/DTOs as appropriate for stronger request validation.
- Add Flyway/Liquibase migrations to create indexes and unique constraints explicitly and to normalize existing enum column values.
- Add unit and integration tests to verify converters handle mixed-case DB values and that repository queries use the index.

If you want, I can also:
- Produce a DB migration script to normalize existing fuel_type/status values and create indexes explicitly.
- Add validation to the Ambulances DTO or entity and update controller to return 400 for invalid input.


