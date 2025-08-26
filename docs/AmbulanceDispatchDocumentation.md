# AmbulanceDispatch Entity and Services Documentation

## Overview
The `AmbulanceDispatch` entity is a core component of the Medilink backend system. It represents the dispatch details of ambulances for emergency and non-emergency medical services. This documentation provides an in-depth explanation of the `AmbulanceDispatch` entity, its associated services, interfaces, and how to ensure they are functioning as expected.

---

## AmbulanceDispatch Entity

### Location
`src/main/java/com/example/codebasebackend/Entities/AmbulanceDispatch.java`

### Fields
The `AmbulanceDispatch` entity contains the following fields:

- **id**: Unique identifier for the dispatch (auto-generated).
- **incidentId**: A unique identifier for the incident (e.g., external or system reference).
- **incidentType**: Type of incident (e.g., TRAFFIC_ACCIDENT, CARDIAC_ARREST).
- **callerName**: Name of the person requesting the ambulance.
- **callerPhone**: Contact number of the caller.
- **callerNotes**: Additional notes provided by the caller.
- **ambulanceUnitId**: Internal identifier for the ambulance unit.
- **vehiclePlate**: License plate of the ambulance.
- **driverName**: Name of the ambulance driver.
- **medicName**: Name of the medic onboard.
- **pickupAddressLine1**: Address line 1 for the pickup location.
- **pickupAddressLine2**: Address line 2 for the pickup location.
- **pickupCity**: City of the pickup location.
- **pickupLatitude**: Latitude of the pickup location.
- **pickupLongitude**: Longitude of the pickup location.
- **dropoffAddressLine1**: Address line 1 for the dropoff location.
- **dropoffCity**: City of the dropoff location.
- **status**: Current status of the dispatch (e.g., REQUESTED, DISPATCHED, COMPLETED).
- **priority**: Priority level of the dispatch (e.g., LOW, MEDIUM, HIGH, CRITICAL).
- **requestTime**: Timestamp when the dispatch was requested.

### Annotations
- `@Entity`: Marks this class as a JPA entity.
- `@Id`: Specifies the primary key.
- `@GeneratedValue`: Configures the generation strategy for the primary key.
- `@Column`: Customizes column properties (e.g., length, uniqueness).
- `@NotBlank`: Ensures mandatory fields are not blank.
- `@Pattern`: Validates the format of the caller's phone number.

---

## Services and Interfaces

### AmbulanceService Interface

#### Location
`src/main/java/com/example/codebasebackend/services/AmbulanceService.java`

#### Methods
- **`createDispatch(AssistanceRequest request)`**: Creates a new ambulance dispatch.
- **`getAllDispatches()`**: Retrieves all ambulance dispatches.
- **`getDispatchById(Long id)`**: Retrieves a specific dispatch by ID.
- **`updateDispatch(Long id, AssistanceRequest request)`**: Updates an existing dispatch.
- **`deleteDispatch(Long id)`**: Deletes a dispatch by ID.
- **`trackDispatch(Long id)`**: Tracks the location of a dispatched ambulance.

### AmbulanceServiceImplementation Class

#### Location
`src/main/java/com/example/codebasebackend/services/AmbulanceServiceImplementation.java`

#### Description
This class implements the `AmbulanceService` interface and provides the business logic for managing ambulance dispatches.

#### Key Methods
- **`createDispatch`**: Maps the `AssistanceRequest` to an `AmbulanceDispatch` entity and saves it to the database.
- **`getAllDispatches`**: Fetches all dispatch records from the database.
- **`getDispatchById`**: Retrieves a dispatch by its unique ID.
- **`updateDispatch`**: Updates dispatch details based on the provided request.
- **`deleteDispatch`**: Deletes a dispatch record if it exists.
- **`trackDispatch`**: Provides tracking information for a specific dispatch.

---

## Controller

### AmbulanceDispatchController

#### Location
`src/main/java/com/example/codebasebackend/controllers/AmbulanceDispatchController.java`

#### Endpoints
- **`POST /api/assist`**: Creates a new ambulance dispatch.
- **`GET /api/assist`**: Retrieves all ambulance dispatches.
- **`GET /api/assist/{id}`**: Retrieves a specific dispatch by ID.
- **`PUT /api/assist/{id}`**: Updates an existing dispatch.
- **`DELETE /api/assist/{id}`**: Deletes a dispatch by ID.
- **`GET /api/assist/{id}/track`**: Tracks the location of a dispatched ambulance.

---

## Testing and Validation

### Unit Tests
- Ensure all methods in `AmbulanceServiceImplementation` are covered by unit tests.
- Mock dependencies (e.g., `AmbulanceDispatchRepository`) to isolate service logic.

### Integration Tests
- Test the `AmbulanceDispatchController` endpoints using tools like Postman or automated frameworks (e.g., Spring Boot Test).
- Validate the request and response payloads.

### Sample Test Cases
1. **Create Dispatch**:
    - Input: Valid `AssistanceRequest`.
    - Expected: Dispatch is created, and the response contains the dispatch details.

2. **Get All Dispatches**:
    - Input: None.
    - Expected: List of all dispatches.

3. **Track Dispatch**:
    - Input: Valid dispatch ID.
    - Expected: Returns tracking information for the dispatch.

4. **Delete Dispatch**:
    - Input: Valid dispatch ID.
    - Expected: Dispatch is deleted, and no content is returned.

---

## Best Practices
- Use meaningful incident IDs to ensure traceability.
- Validate all input data to prevent invalid dispatch records.
- Log all dispatch operations for auditing purposes.
- Ensure the `trackDispatch` method integrates with real-time location services if applicable.

---

## Conclusion
The `AmbulanceDispatch` entity and its associated services provide a robust framework for managing ambulance dispatch operations. By following the outlined documentation, developers can ensure the system is implemented and tested effectively.
