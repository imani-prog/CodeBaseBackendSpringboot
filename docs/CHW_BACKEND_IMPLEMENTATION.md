# Community Health Workers Module - Backend Implementation

## Overview
This document describes the backend enhancements made to support the Community Health Workers (CHW) management features. The implementation adds performance tracking, regional assignments, and additional operational fields to the existing CommunityHealthWorkers module.

## Implementation Date
January 12, 2026

---

## Changes Summary

### 1. Entity Updates - `CommunityHealthWorkers.java`

#### New Fields Added

##### Regional Assignment
- `region` (String, max 100 chars) - Regional assignment (e.g., "Nairobi", "Mombasa", "Kisumu")

##### Workload Management
- `assignedPatients` (Integer, default: 0) - Number of patients assigned to this CHW
- `startDate` (LocalDate) - Employment/service start date
- `lastStatusUpdate` (OffsetDateTime) - Timestamp when status was last changed

##### Performance Metrics
- `monthlyVisits` (Integer, default: 0) - Number of visits this month
- `successRate` (BigDecimal 5,2, default: 0.00) - Success rate percentage (0-100)
- `responseTime` (String, max 50 chars) - Average response time (e.g., "1.8hrs", "2.5hrs")
- `rating` (BigDecimal 3,2, default: 0.00) - Rating out of 5.0 (e.g., 4.8)

#### Database Indexes
Added indexes for better query performance:
- `idx_chw_region` - Index on region field
- `idx_chw_start_date` - Index on startDate field
- Existing: `idx_chw_status`, `idx_chw_city`, `idx_chw_code`

#### Entity Lifecycle Methods
- **@PrePersist**: Sets default values for new fields (assignedPatients=0, monthlyVisits=0, successRate=0, rating=0, lastStatusUpdate=now())
- **@PreUpdate**: Updates lastStatusUpdate when entity changes

---

### 2. DTO Updates

#### `CommunityHealthWorkerRequest.java`
Added fields with validation:
- `region` (String)
- `assignedPatients` (Integer) - @Min(0)
- `startDate` (LocalDate)
- `monthlyVisits` (Integer) - @Min(0)
- `successRate` (BigDecimal) - @DecimalMin("0.00"), @DecimalMax("100.00")
- `responseTime` (String) - @Pattern for "1.8hrs" format
- `rating` (BigDecimal) - @DecimalMin("0.0"), @DecimalMax("5.0")
- `status` (String) - AVAILABLE, BUSY, OFFLINE

#### `CommunityHealthWorkerResponse.java`
Added fields:
- All fields from request
- `lastStatusUpdate` (OffsetDateTime)
- **Computed fields**:
  - `fullName` (String) - firstName + middleName + lastName
  - `avatar` (String) - Initials (e.g., "GA" for Grace Akinyi)

#### `PerformanceMetricsRequest.java` (NEW)
New DTO for updating performance metrics:
- `monthlyVisits` (Integer) - @Min(0)
- `successRate` (BigDecimal) - @DecimalMin("0.00"), @DecimalMax("100.00")
- `responseTime` (String) - @Pattern for "X.Xhrs" format
- `rating` (BigDecimal) - @DecimalMin("0.0"), @DecimalMax("5.0")

---

### 3. Repository Updates - `CommunityHealthWorkersRepository.java`

#### New Query Methods
```java
Optional<CommunityHealthWorkers> findByCode(String code);
List<CommunityHealthWorkers> findByRegion(String region);
List<CommunityHealthWorkers> findByCity(String city);
List<CommunityHealthWorkers> findByRegionAndStatus(String region, Status status);

@Query Page<CommunityHealthWorkers> searchWithFilters(
    String region, Status status, String city, Pageable pageable);

Long countByRegion(String region);
Long countByStatus(Status status);
```

---

### 4. Service Updates

#### `CommunityHealthWorkersService.java` (Interface)
New method signatures:
```java
CommunityHealthWorkerResponse updatePerformanceMetrics(Long id, PerformanceMetricsRequest request);
List<CommunityHealthWorkerResponse> findByRegion(String region);
List<CommunityHealthWorkerResponse> findByStatus(Status status);
Page<CommunityHealthWorkerResponse> search(String region, Status status, String city, 
                                            int page, int size, String sortBy, String sortDirection);
```

#### `CommunityHealthWorkersServiceImplementation.java`
##### Updated Methods:
- `update()` - Now tracks status changes and updates `lastStatusUpdate` timestamp
- `apply()` - Updated to handle all new fields
- `toResponse()` - Maps all new fields including computed fields

##### New Private Methods:
- `buildFullName()` - Constructs full name from first, middle, and last names
- `buildAvatar()` - Generates two-letter initials from first and last names

##### New Service Methods:
All interface methods implemented with proper transaction handling and error management.

---

### 5. Controller Updates - `CommunityHealthWorkersController.java`

#### New Endpoints

##### 1. Update Performance Metrics
```http
PATCH /api/chw/{id}/performance
Content-Type: application/json

Request Body: PerformanceMetricsRequest
Response: CommunityHealthWorkerResponse
```

##### 2. Get CHWs by Region
```http
GET /api/chw/by-region/{region}

Response: List<CommunityHealthWorkerResponse>
```

##### 3. Get CHWs by Status
```http
GET /api/chw/by-status/{status}

Response: List<CommunityHealthWorkerResponse>
```

##### 4. Advanced Search with Filters
```http
GET /api/chw/search?region={region}&status={status}&city={city}&page={page}&size={size}&sortBy={sortBy}&sortDirection={sortDirection}

Query Parameters:
- region (optional)
- status (optional) - AVAILABLE, BUSY, OFFLINE
- city (optional)
- page (default: 0)
- size (default: 10)
- sortBy (default: "id")
- sortDirection (default: "ASC")

Response: Page<CommunityHealthWorkerResponse>
```

#### Updated Endpoints
All endpoints now return computed fields (`fullName`, `avatar`) and new fields in responses.

---

### 6. Database Migration

Migration file: `V2__add_chw_performance_and_regional_fields.sql`

The migration:
- Adds all new columns to `community_health_workers` table
- Creates new indexes on `region` and `startDate`
- Sets default values for existing records
- Updates `lastStatusUpdate` from `updated_at` for existing records

---

### 7. Testing

#### Unit Tests - `CommunityHealthWorkersServiceTest.java`
Tests cover:
- Creating CHW with new fields
- Updating performance metrics
- Finding by region
- Finding by status
- Advanced search with filters
- Full name building logic
- Avatar generation
- Null handling for optional fields

**Test Results**: ✅ All 9 tests passing

#### Integration Tests - `CommunityHealthWorkersControllerIntegrationTest.java`
Tests cover:
- All new endpoints
- Request validation
- Response structure
- Computed fields in responses

---

## API Usage Examples

### Create CHW with Regional Assignment
```bash
curl -X POST http://localhost:8080/api/chw \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Grace",
    "lastName": "Akinyi",
    "email": "grace@example.com",
    "phone": "+254712345678",
    "region": "Nairobi",
    "city": "Nairobi",
    "assignedPatients": 0,
    "startDate": "2024-01-01"
  }'
```

### Update Performance Metrics
```bash
curl -X PATCH http://localhost:8080/api/chw/1/performance \
  -H "Content-Type: application/json" \
  -d '{
    "monthlyVisits": 50,
    "successRate": 95.5,
    "responseTime": "1.8hrs",
    "rating": 4.8
  }'
```

### Search CHWs
```bash
curl "http://localhost:8080/api/chw/search?region=Nairobi&status=AVAILABLE&page=0&size=10"
```

### Get by Region
```bash
curl http://localhost:8080/api/chw/by-region/Nairobi
```

### Get by Status
```bash
curl http://localhost:8080/api/chw/by-status/AVAILABLE
```

---

## Response Example

```json
{
  "id": 1,
  "code": "CHW001",
  "firstName": "Grace",
  "middleName": "Wanjiru",
  "lastName": "Akinyi",
  "fullName": "Grace Wanjiru Akinyi",
  "avatar": "GA",
  "email": "grace@example.com",
  "phone": "+254712345678",
  "city": "Nairobi",
  "state": "Nairobi County",
  "country": "Kenya",
  "region": "Nairobi",
  "assignedPatients": 15,
  "startDate": "2024-01-01",
  "lastStatusUpdate": "2026-01-12T22:00:00+03:00",
  "monthlyVisits": 45,
  "successRate": 95.50,
  "responseTime": "1.8hrs",
  "rating": 4.8,
  "status": "AVAILABLE",
  "specialization": "Maternal Health",
  "hospitalId": 1,
  "latitude": -1.286389,
  "longitude": 36.817223,
  "createdAt": "2024-01-01T10:00:00+03:00",
  "updatedAt": "2026-01-12T22:00:00+03:00"
}
```

---

## Validation Rules

### Performance Metrics
- `monthlyVisits`: Must be >= 0
- `successRate`: Must be between 0.00 and 100.00
- `responseTime`: Must match pattern "X.Xhrs" (e.g., "1.8hrs", "2.5hrs")
- `rating`: Must be between 0.0 and 5.0

### Request Fields
- `assignedPatients`: Must be >= 0
- `status`: Must be one of AVAILABLE, BUSY, OFFLINE

---

## Audit Logging

All endpoints are decorated with `@Auditable` annotation:
- CREATE operations log the entity ID and arguments
- READ operations log the entity ID
- UPDATE operations log the entity ID and arguments
- DELETE operations log the entity ID

Event types: `CREATE`, `READ`, `UPDATE`, `DELETE`
Entity type: `"CHW"`

---

## Performance Considerations

### Indexes
Queries on `region`, `status`, `city`, and `startDate` are optimized with database indexes.

### Pagination
The search endpoint supports pagination to handle large datasets efficiently.

### Transaction Management
- Read-only transactions for query operations
- Write transactions for create/update operations
- Proper transaction boundaries to ensure data consistency

---

## Backward Compatibility

✅ All existing endpoints continue to work
✅ Existing API consumers will receive additional fields in responses
✅ New fields are optional in request DTOs
✅ Database migration handles existing records with default values

---

## Future Enhancements

Consider implementing:
1. Historical tracking of performance metrics over time
2. Region validation against predefined list
3. Automated performance metric calculations
4. Alerts for CHWs with low ratings or high workload
5. Regional dashboard with aggregated statistics
6. CHW availability scheduling

---

## Files Modified/Created

### Modified Files
- `CommunityHealthWorkers.java` - Entity with new fields
- `CommunityHealthWorkerRequest.java` - Added new request fields
- `CommunityHealthWorkerResponse.java` - Added new response fields
- `CommunityHealthWorkersRepository.java` - Added query methods
- `CommunityHealthWorkersService.java` - Added method signatures
- `CommunityHealthWorkersServiceImplementation.java` - Implemented new methods
- `CommunityHealthWorkersController.java` - Added new endpoints

### New Files
- `PerformanceMetricsRequest.java` - New DTO
- `V2__add_chw_performance_and_regional_fields.sql` - Database migration
- `CommunityHealthWorkersServiceTest.java` - Unit tests
- `CommunityHealthWorkersControllerIntegrationTest.java` - Integration tests
- `CHW_BACKEND_IMPLEMENTATION.md` - This documentation

---

## Build & Test Results

### Compilation
✅ Build successful - No compilation errors

### Unit Tests
✅ 9/9 tests passing

### Integration Tests
Available for manual execution

---

## Support

For questions or issues, contact the development team or refer to:
- API Documentation: `/swagger-ui.html` (when running)
- Project Documentation: `/docs/` folder
- Issue Tracker: [Project Repository]

---

**Implementation completed successfully on January 12, 2026**

