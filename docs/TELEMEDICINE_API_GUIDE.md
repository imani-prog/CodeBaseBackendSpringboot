# Telemedicine Session API Guide

## Overview
This document provides comprehensive documentation for the Telemedicine Session API endpoints in the Medilink Backend system.

## Base URL
```
http://localhost:8080/api/telemedicine/sessions
```

## Available Resources

### Test Data

#### Doctors
| ID | Name | Specialty | Email | Phone |
|----|------|-----------|-------|-------|
| 2 | Dr. John Mwangi | Pediatric Surgeon | john.mwangi@example.com | +254723456789 |
| 10 | Dr. Emily Carter | Pediatrician | emily.carter@medilink.com | +254712345678 |
| 11 | Dr. Michael Ochieng | Cardiologist | michael.ochieng@medilink.com | +254723456789 |
| 12 | Dr. Sarah Kamau | Neurologist | sarah.kamau@medilink.com | +254734567890 |
| 13 | Dr. David Mwangi | Orthopedic Surgeon | david.mwangi@medilink.com | +254745678901 |
| 14 | Dr. Grace Wanjiku | Bariatric Surgeon | grace.wanjiku@medilink.com | +254756789012 |
| 15 | Dr. James Otieno | Psychiatrist | james.otieno@medilink.com | +254767890123 |
| 16 | Dr. Linda Njeri | Dermatologist | linda.njeri@medilink.com | +254778901234 |
| 17 | Dr. Robert Kariuki | Pediatric Surgeon | robert.kariuki@medilink.com | +254789012345 |
| 18 | Dr. Patricia Mutua | Gastroenterologist | patricia.mutua@medilink.com | +254790123456 |
| 19 | Dr. Daniel Kipchoge | Neurosurgeon | daniel.kipchoge@medilink.com | +254701234567 |

#### Patients
| ID | Name | Gender | Conditions | City | Phone |
|----|------|--------|------------|------|-------|
| 4 | John Mwangi | Male | Diabetes | Nairobi | +254700123456 |
| 5 | Aisha Ali | Female | Hypertension | Mombasa | +254701223344 |
| 6 | Otieno Ouma | Male | Asthma | Kisumu | +254720334455 |
| 7 | Chebet Koech | Female | Asthma | Eldoret | +254728445566 |
| 8 | Wanjiru Kamau | Female | Hypertension, Diabetes | Nakuru | +254733667788 |

#### Hospitals
| ID | Name | Type | City |
|----|------|------|------|
| 1 | City General Hospital | General | Nairobi |
| 2 | Mombasa Coast Hospital | Specialty | Mombasa |
| 3 | Eldoret Regional Hospital | Teaching | Eldoret |
| 4 | Kisumu Lakeside Medical Center | Clinic | Kisumu |
| 5 | Nakuru District Hospital | General | Nakuru |

## API Endpoints

### 1. Session Creation

#### Create Telemedicine Session
**Endpoint:** `POST /api/telemedicine/sessions`

**Description:** Creates a new telemedicine session between a patient and doctor.

**Request Body:**
```json
{
  "patientId": 4,
  "doctorId": 11,
  "hospitalId": 1,
  "sessionType": "CONSULTATION",
  "platform": "VIDEO_CALL",
  "priority": "NORMAL",
  "startTime": "2026-02-10T14:00:00Z",
  "plannedDuration": 30,
  "symptoms": ["High blood sugar", "Fatigue"],
  "chiefComplaint": "Patient reports uncontrolled blood sugar",
  "cost": 2500.00,
  "paymentMethod": "MPESA",
  "insuranceCovered": true,
  "notes": "Requires medication review"
}
```

**Response:** `201 CREATED`
```json
{
  "id": 1,
  "sessionId": "TM-001",
  "patientId": 4,
  "patientName": "John Mwangi",
  "patientEmail": "john.mwangi@example.com",
  "patientPhone": "+254700123456",
  "doctorId": 11,
  "doctorName": "Dr. Michael Ochieng",
  "doctorPhoto": "https://cdn.medilink.com/photos/dr-michael-ochieng.jpg",
  "doctorSpecialty": "Cardiology",
  "doctorRating": 4.8,
  "hospitalId": 1,
  "hospitalName": "City General Hospital",
  "sessionType": "CONSULTATION",
  "platform": "VIDEO_CALL",
  "status": "SCHEDULED",
  "priority": "NORMAL",
  "startTime": "2026-02-10T14:00:00Z",
  "plannedDuration": 30,
  "symptoms": ["High blood sugar", "Fatigue"],
  "chiefComplaint": "Patient reports uncontrolled blood sugar",
  "cost": 2500.00,
  "paymentMethod": "MPESA",
  "insuranceCovered": true,
  "notes": "Requires medication review",
  "createdAt": "2026-02-05T10:30:00Z",
  "updatedAt": "2026-02-05T10:30:00Z"
}
```

### 2. Session Retrieval

#### Get All Sessions (Paginated)
**Endpoint:** `GET /api/telemedicine/sessions?page=0&size=10&sort=startTime,desc`

**Response:** `200 OK`
```json
{
  "content": [...],
  "pageable": {...},
  "totalPages": 5,
  "totalElements": 48,
  "size": 10,
  "number": 0
}
```

#### Get Session by ID
**Endpoint:** `GET /api/telemedicine/sessions/{id}`

**Example:** `GET /api/telemedicine/sessions/1`

**Response:** `200 OK` (Same structure as create response)

#### Get Session by Session ID
**Endpoint:** `GET /api/telemedicine/sessions/by-session-id/{sessionId}`

**Example:** `GET /api/telemedicine/sessions/by-session-id/TM-001`

**Response:** `200 OK`

### 3. Session Updates

#### Update Session
**Endpoint:** `PUT /api/telemedicine/sessions/{id}`

**Request Body:** Same as create session request

**Response:** `200 OK`

#### Delete Session
**Endpoint:** `DELETE /api/telemedicine/sessions/{id}`

**Response:** `204 NO CONTENT`

### 4. Session State Management

#### Start Session
**Endpoint:** `POST /api/telemedicine/sessions/{id}/start`

**Description:** Marks session as ACTIVE and records actual start time.

**Response:** `200 OK`

#### Pause Session
**Endpoint:** `POST /api/telemedicine/sessions/{id}/pause`

**Description:** Temporarily pauses an active session.

**Response:** `200 OK`

#### Resume Session
**Endpoint:** `POST /api/telemedicine/sessions/{id}/resume`

**Description:** Resumes a paused session.

**Response:** `200 OK`

#### Complete Session
**Endpoint:** `POST /api/telemedicine/sessions/{id}/complete`

**Query Parameters:**
- `diagnosis` (required): Medical diagnosis
- `prescription` (optional): Prescribed medications
- `doctorNotes` (optional): Doctor's notes

**Example:**
```
POST /api/telemedicine/sessions/1/complete?diagnosis=Type 2 Diabetes Mellitus&prescription=Metformin 500mg&doctorNotes=Patient showing improvement
```

**Response:** `200 OK`

#### Cancel Session
**Endpoint:** `POST /api/telemedicine/sessions/{id}/cancel`

**Query Parameters:**
- `reason` (required): Cancellation reason

**Example:**
```
POST /api/telemedicine/sessions/1/cancel?reason=Patient emergency
```

**Response:** `200 OK`

#### Terminate Session
**Endpoint:** `POST /api/telemedicine/sessions/{id}/terminate`

**Query Parameters:**
- `reason` (required): Termination reason

**Response:** `200 OK`

#### Rate Session
**Endpoint:** `POST /api/telemedicine/sessions/{id}/rate`

**Query Parameters:**
- `rating` (required): Rating from 1-5
- `feedback` (optional): Patient feedback

**Example:**
```
POST /api/telemedicine/sessions/1/rate?rating=5&feedback=Excellent service
```

**Response:** `200 OK`

### 5. Filtering and Search

#### Get Sessions by Status
**Endpoint:** `GET /api/telemedicine/sessions/by-status/{status}`

**Valid Status Values:**
- SCHEDULED
- ACTIVE
- PAUSED
- COMPLETED
- CANCELLED
- TERMINATED
- NO_SHOW

**Example:** `GET /api/telemedicine/sessions/by-status/SCHEDULED`

#### Get Sessions by Platform
**Endpoint:** `GET /api/telemedicine/sessions/by-platform/{platform}`

**Valid Platform Values:**
- VIDEO_CALL
- AUDIO_CALL
- MESSAGING

**Example:** `GET /api/telemedicine/sessions/by-platform/VIDEO_CALL`

#### Get Active Sessions by Priority
**Endpoint:** `GET /api/telemedicine/sessions/by-priority/{priority}`

**Valid Priority Values:**
- LOW
- NORMAL
- MEDIUM
- HIGH
- URGENT

**Example:** `GET /api/telemedicine/sessions/by-priority/URGENT`

#### Search Sessions
**Endpoint:** `GET /api/telemedicine/sessions/search`

**Query Parameters:**
- `term` (required): Search term
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

**Example:** `GET /api/telemedicine/sessions/search?term=diabetes&page=0&size=10`

#### Advanced Filter
**Endpoint:** `GET /api/telemedicine/sessions/filter`

**Query Parameters:**
- `status` (optional): Session status
- `platform` (optional): Platform type
- `priority` (optional): Priority level
- `doctorId` (optional): Doctor ID
- `startDate` (optional): Start date (ISO 8601)
- `endDate` (optional): End date (ISO 8601)
- `page` (optional): Page number
- `size` (optional): Page size

**Example:**
```
GET /api/telemedicine/sessions/filter?status=SCHEDULED&platform=VIDEO_CALL&priority=NORMAL&doctorId=11&startDate=2026-02-10T00:00:00Z&endDate=2026-02-28T23:59:59Z
```

#### Get Sessions by Patient
**Endpoint:** `GET /api/telemedicine/sessions/by-patient/{patientId}`

**Example:** `GET /api/telemedicine/sessions/by-patient/4`

#### Get Sessions by Doctor
**Endpoint:** `GET /api/telemedicine/sessions/by-doctor/{doctorId}`

**Example:** `GET /api/telemedicine/sessions/by-doctor/11`

#### Get Active Doctor Sessions
**Endpoint:** `GET /api/telemedicine/sessions/doctor/{doctorId}/active`

**Description:** Returns all currently active sessions for a specific doctor.

**Example:** `GET /api/telemedicine/sessions/doctor/11/active`

### 6. Analytics and Reporting

#### Get Platform Overview
**Endpoint:** `GET /api/telemedicine/sessions/overview`

**Response:**
```json
{
  "totalSessions": 150,
  "activeSessions": 12,
  "scheduledSessions": 45,
  "completedToday": 23,
  "averageRating": 4.6,
  "totalRevenue": 125000.00
}
```

#### Get Revenue Data
**Endpoint:** `GET /api/telemedicine/sessions/revenue`

**Query Parameters:**
- `period` (optional): "daily", "weekly", or "monthly" (default: "monthly")

**Example:** `GET /api/telemedicine/sessions/revenue?period=monthly`

**Response:**
```json
{
  "period": "monthly",
  "totalRevenue": 125000.00,
  "sessionCount": 150,
  "averagePerSession": 833.33,
  "breakdown": [
    {
      "date": "2026-02-01",
      "revenue": 15000.00,
      "sessions": 20
    }
  ]
}
```

#### Get Platform Stats
**Endpoint:** `GET /api/telemedicine/sessions/platform-stats`

**Response:**
```json
{
  "videoCallSessions": 85,
  "audioCallSessions": 45,
  "messagingSessions": 20,
  "mostUsedPlatform": "VIDEO_CALL",
  "platformUsagePercentage": {
    "VIDEO_CALL": 56.7,
    "AUDIO_CALL": 30.0,
    "MESSAGING": 13.3
  }
}
```

#### Get Online Doctors
**Endpoint:** `GET /api/telemedicine/sessions/doctors/online`

**Response:**
```json
[
  {
    "doctorId": 11,
    "doctorName": "Dr. Michael Ochieng",
    "specialty": "Cardiology",
    "status": "ONLINE",
    "activeSessions": 2,
    "rating": 4.8
  }
]
```

#### Get Session History
**Endpoint:** `GET /api/telemedicine/sessions/history`

**Query Parameters:**
- `period` (optional): "today", "week", or "month" (default: "today")

**Example:** `GET /api/telemedicine/sessions/history?period=week`

#### Send Session Reminders
**Endpoint:** `POST /api/telemedicine/sessions/send-reminders`

**Description:** Sends reminders to patients with upcoming sessions.

**Response:** `200 OK`

## Enumerations

### SessionType
- `CONSULTATION` - General medical consultation
- `FOLLOW_UP` - Follow-up appointment
- `EMERGENCY` - Emergency consultation
- `PRESCRIPTION` - Prescription renewal/review
- `MENTAL_HEALTH` - Mental health counseling
- `SPECIALIST_REFERRAL` - Specialist consultation

### PlatformType
- `VIDEO_CALL` - Video consultation
- `AUDIO_CALL` - Audio-only call
- `MESSAGING` - Text-based consultation

### Priority
- `LOW` - Low priority
- `NORMAL` - Normal priority
- `MEDIUM` - Medium priority
- `HIGH` - High priority
- `URGENT` - Urgent/Emergency priority

### SessionStatus
- `SCHEDULED` - Session scheduled for future
- `ACTIVE` - Session currently in progress
- `PAUSED` - Session temporarily paused
- `COMPLETED` - Session successfully completed
- `CANCELLED` - Session cancelled
- `TERMINATED` - Session forcefully ended
- `NO_SHOW` - Patient didn't join

### PaymentMethod
- `MPESA` - Mobile money payment
- `CARD` - Credit/Debit card
- `CASH` - Cash payment
- `INSURANCE` - Insurance coverage
- `BANK_TRANSFER` - Bank transfer

## Common Use Cases

### Use Case 1: Emergency Consultation
```json
{
  "patientId": 5,
  "doctorId": 11,
  "sessionType": "EMERGENCY",
  "platform": "VIDEO_CALL",
  "priority": "URGENT",
  "startTime": "2026-02-05T18:30:00Z",
  "plannedDuration": 20,
  "symptoms": ["Chest pain", "Shortness of breath"],
  "chiefComplaint": "Sudden chest pain",
  "cost": 3500.00,
  "paymentMethod": "INSURANCE",
  "insuranceCovered": true
}
```

### Use Case 2: Routine Follow-up
```json
{
  "patientId": 4,
  "doctorId": 11,
  "sessionType": "FOLLOW_UP",
  "platform": "AUDIO_CALL",
  "priority": "NORMAL",
  "startTime": "2026-02-15T10:00:00Z",
  "plannedDuration": 15,
  "symptoms": ["Follow-up check"],
  "chiefComplaint": "Routine diabetes follow-up",
  "cost": 1500.00,
  "paymentMethod": "MPESA",
  "insuranceCovered": true
}
```

### Use Case 3: Mental Health Session
```json
{
  "patientId": 7,
  "doctorId": 15,
  "sessionType": "MENTAL_HEALTH",
  "platform": "VIDEO_CALL",
  "priority": "MEDIUM",
  "startTime": "2026-02-12T15:00:00Z",
  "plannedDuration": 45,
  "symptoms": ["Anxiety", "Panic attacks"],
  "chiefComplaint": "Frequent anxiety attacks",
  "cost": 3000.00,
  "paymentMethod": "MPESA",
  "insuranceCovered": true
}
```

## Error Handling

### Common Error Responses

#### 400 Bad Request
```json
{
  "timestamp": "2026-02-05T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "patientId",
      "message": "Patient ID is required"
    }
  ]
}
```

#### 404 Not Found
```json
{
  "timestamp": "2026-02-05T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Session not found with ID: 999"
}
```

#### 409 Conflict
```json
{
  "timestamp": "2026-02-05T10:30:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Doctor is not available at this time"
}
```

## Testing with IntelliJ HTTP Client

1. Open the `telemedicine-api-examples.http` file in IntelliJ IDEA
2. Click on the green "play" button next to any request
3. View the response in the built-in HTTP client panel
4. Modify request bodies as needed for testing

## Best Practices

1. **Always validate doctor availability** before creating sessions
2. **Use appropriate priority levels** for emergency vs routine consultations
3. **Include detailed symptoms and complaints** for better medical records
4. **Set realistic planned durations** based on session type
5. **Update session status** as it progresses through lifecycle
6. **Collect patient feedback** after session completion
7. **Handle timezone conversions** properly when scheduling

## Session Lifecycle

```
SCHEDULED → START → ACTIVE → COMPLETE → COMPLETED
                      ↓
                   PAUSE → RESUME → ACTIVE
```

Alternative paths:
- `SCHEDULED → CANCEL → CANCELLED`
- `ACTIVE → TERMINATE → TERMINATED`
- `SCHEDULED → (patient no-show) → NO_SHOW`

## Date/Time Format

All dates and times use ISO 8601 format with timezone:
```
2026-02-10T14:00:00Z
```

For East Africa Time (EAT = UTC+3):
```
2026-02-10T17:00:00+03:00
```

## Support

For issues or questions, contact the Medilink development team.
