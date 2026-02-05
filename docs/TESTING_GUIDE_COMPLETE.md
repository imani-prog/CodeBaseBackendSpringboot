# Telemedicine API - Complete Testing Guide

## 🎯 Problem Solved

**Original Issue:** Session ID format mismatch  
- ❌ You tried: `TELE-2026-00001`  
- ✅ Actual format: `TM-001`

**Status:** ✅ All files updated and ready to use!

---

## 📁 Updated Files

### 1. Main API Examples
**File:** `docs/telemedicine-api-examples.http`  
**Contains:** 42+ complete API request examples with all CRUD operations

**Key Features:**
- ✅ All session creation examples (12 different scenarios)
- ✅ Session state management (start, pause, resume, complete, cancel)
- ✅ Filtering and search operations
- ✅ Analytics and reporting endpoints
- ✅ Now includes section with examples using your existing sessions

### 2. Quick Test File
**File:** `docs/telemedicine-quick-tests.http`  
**Contains:** 20 quick tests using your actual session data

**Use this for:**
- ✅ Rapid testing with existing sessions (TM-001 through TM-010)
- ✅ Verifying session queries work correctly
- ✅ Testing search and filter functionality

### 3. Complete Documentation
**File:** `docs/TELEMEDICINE_API_GUIDE.md`  
**Contains:** Full API documentation with examples, enums, and best practices

### 4. Sample JSON Payloads
**File:** `docs/telemedicine-samples.json`  
**Contains:** Copy-paste ready JSON payloads for all scenarios

### 5. Fix Documentation
**File:** `docs/SESSION_ID_FIX.md`  
**Contains:** Detailed explanation of the session ID format issue and fix

---

## 🗄️ Your Current Database Sessions

| ID | Session ID | Patient | Doctor | Chief Complaint | Status |
|----|------------|---------|--------|----------------|--------|
| 1 | TM-001 | John Mwangi (4) | Dr. Michael Ochieng (11) | Uncontrolled blood sugar | SCHEDULED |
| 2 | TM-002 | Aisha Ali (5) | Dr. Michael Ochieng (11) | Chest pain | SCHEDULED |
| 3 | TM-003 | Otieno Ouma (6) | Dr. Linda Njeri (16) | Skin rash | SCHEDULED |
| 4 | TM-004 | Chebet Koech (7) | Dr. James Otieno (15) | Anxiety attacks | SCHEDULED |
| 5 | TM-005 | Wanjiru Kamau (8) | Dr. Michael Ochieng (11) | Prescription renewal | SCHEDULED |
| 6 | TM-006 | Otieno Ouma (6) | Dr. Emily Carter (10) | Asthma follow-up | SCHEDULED |
| 7 | TM-007 | Otieno Ouma (6) | Dr. Emily Carter (10) | Asthma follow-up | SCHEDULED |
| 8 | TM-008 | Otieno Ouma (6) | Dr. Emily Carter (10) | Asthma follow-up | SCHEDULED |
| 9 | TM-009 | John Mwangi (4) | Dr. Patricia Mutua (18) | Abdominal discomfort | SCHEDULED |
| 10 | TM-010 | Chebet Koech (7) | Dr. David Mwangi (13) | Knee pain | SCHEDULED |

---

## 🚀 Quick Start - Test These Now!

### 1️⃣ Get Session by Session ID (Now Fixed!)
```http
GET http://localhost:8080/api/telemedicine/sessions/by-session-id/TM-001
```
**Expected:** 200 OK with full session details

### 2️⃣ Get All Sessions
```http
GET http://localhost:8080/api/telemedicine/sessions
```
**Expected:** 200 OK with paginated list of all 10 sessions

### 3️⃣ Get Sessions for Patient 6 (Multiple Results)
```http
GET http://localhost:8080/api/telemedicine/sessions/by-patient/6
```
**Expected:** 200 OK with 4 sessions (IDs: 3, 6, 7, 8)

### 4️⃣ Get Sessions for Dr. Michael Ochieng
```http
GET http://localhost:8080/api/telemedicine/sessions/by-doctor/11
```
**Expected:** 200 OK with 3 sessions (IDs: 1, 2, 5)

### 5️⃣ Search for "diabetes"
```http
GET http://localhost:8080/api/telemedicine/sessions/search?term=diabetes
```
**Expected:** 200 OK with sessions matching "diabetes"

### 6️⃣ Start Emergency Session
```http
POST http://localhost:8080/api/telemedicine/sessions/2/start
```
**Expected:** 200 OK, status changes from SCHEDULED to ACTIVE

### 7️⃣ Complete Session with Diagnosis
```http
POST http://localhost:8080/api/telemedicine/sessions/1/complete?diagnosis=Type 2 Diabetes - controlled&prescription=Metformin 500mg twice daily&doctorNotes=Good progress
```
**Expected:** 200 OK, status changes to COMPLETED

### 8️⃣ Rate a Session
```http
POST http://localhost:8080/api/telemedicine/sessions/4/rate?rating=5&feedback=Excellent service
```
**Expected:** 200 OK with updated session including rating

### 9️⃣ Get Platform Overview
```http
GET http://localhost:8080/api/telemedicine/sessions/overview
```
**Expected:** 200 OK with statistics (total sessions, revenue, etc.)

### 🔟 Get Sessions by Status
```http
GET http://localhost:8080/api/telemedicine/sessions/by-status/SCHEDULED
```
**Expected:** 200 OK with all scheduled sessions

---

## 📊 Session Lifecycle Workflow

```
CREATE
  ↓
SCHEDULED → START → ACTIVE → COMPLETE → COMPLETED ✓
                      ↓
                   PAUSE → RESUME → ACTIVE
                   
SCHEDULED → CANCEL → CANCELLED ✗
ACTIVE → TERMINATE → TERMINATED ✗
SCHEDULED → (no-show) → NO_SHOW ✗
```

---

## 🔑 Key Information

### Session ID Format
- **Pattern:** `TM-XXX` (3-digit zero-padded)
- **Example:** TM-001, TM-002, TM-010, TM-100
- **Auto-generated** on session creation

### Two Ways to Query
1. **By Session ID (String):** `/api/telemedicine/sessions/by-session-id/TM-001`
2. **By Database ID (Number):** `/api/telemedicine/sessions/1`

### Available Enums

#### SessionType
- `CONSULTATION` - General consultation
- `FOLLOW_UP` - Follow-up appointment
- `EMERGENCY` - Emergency consultation
- `PRESCRIPTION` - Prescription renewal
- `MENTAL_HEALTH` - Mental health counseling
- `SPECIALIST_REFERRAL` - Specialist consultation

#### PlatformType
- `VIDEO_CALL` - Video consultation
- `AUDIO_CALL` - Audio-only call
- `MESSAGING` - Text-based consultation

#### Priority
- `LOW`, `NORMAL`, `MEDIUM`, `HIGH`, `URGENT`

#### SessionStatus
- `SCHEDULED`, `ACTIVE`, `PAUSED`, `COMPLETED`, `CANCELLED`, `TERMINATED`, `NO_SHOW`

#### PaymentMethod
- `MPESA`, `CARD`, `CASH`, `INSURANCE`, `BANK_TRANSFER`

---

## 📝 Creating New Sessions

### Basic Template
```http
POST http://localhost:8080/api/telemedicine/sessions
Content-Type: application/json

{
  "patientId": 4,
  "doctorId": 11,
  "hospitalId": 1,
  "sessionType": "CONSULTATION",
  "platform": "VIDEO_CALL",
  "priority": "NORMAL",
  "startTime": "2026-02-10T14:00:00Z",
  "plannedDuration": 30,
  "symptoms": ["Symptom 1", "Symptom 2"],
  "chiefComplaint": "Main reason for consultation",
  "cost": 2500.00,
  "paymentMethod": "MPESA",
  "insuranceCovered": true,
  "notes": "Additional notes"
}
```

### Required Fields
- `patientId`, `doctorId`, `sessionType`, `platform`, `priority`
- `startTime`, `plannedDuration`, `symptoms`, `chiefComplaint`
- `cost`, `paymentMethod`, `insuranceCovered`

### Optional Fields
- `hospitalId`, `notes`, `recordingEnabled`

---

## 🧪 Testing Workflow

### Step 1: Open Test Files
In IntelliJ IDEA:
1. Open `docs/telemedicine-quick-tests.http` for quick tests
2. Or open `docs/telemedicine-api-examples.http` for comprehensive examples

### Step 2: Execute Requests
- Click the green "play" button next to any request
- Or use keyboard shortcut (usually Ctrl+Enter or Cmd+Enter)

### Step 3: View Response
- Response appears in the HTTP Client panel
- Check status code (200, 201, 404, etc.)
- Verify response body

### Step 4: Common Testing Scenarios

**Scenario A: Create and Complete Session**
1. Create session (Request #1)
2. Start session (Request #14)
3. Complete session (Request #17)
4. Rate session (Request #20)

**Scenario B: Query Patient History**
1. Get sessions by patient (Request #26)
2. Filter by status (Request #21)
3. Search by term (Request #24)

**Scenario C: Doctor Dashboard**
1. Get sessions by doctor (Request #27)
2. Get active sessions (Request #28)
3. Get platform overview (Request #29)

---

## 🎓 Best Practices

1. ✅ **Always check doctor availability** before creating sessions
2. ✅ **Use appropriate priority levels** (URGENT for emergencies)
3. ✅ **Include detailed symptoms** for better medical records
4. ✅ **Set realistic durations** (15-60 minutes typical)
5. ✅ **Update session status** as it progresses
6. ✅ **Collect feedback** after completion
7. ✅ **Handle timezone conversions** (use ISO 8601 format with Z for UTC)

---

## ❓ Common Issues & Solutions

### Issue: Session Not Found
**Error:** `404 NOT_FOUND "Session not found with ID: TELE-2026-00001"`  
**Solution:** Use correct format: `TM-001` instead of `TELE-2026-00001`

### Issue: Doctor Unavailable
**Error:** `400 BAD_REQUEST "Doctor is currently unavailable"`  
**Solution:** Doctor has 3+ active sessions. Choose different doctor or wait.

### Issue: Cannot Update Session
**Error:** `400 BAD_REQUEST "Only scheduled sessions can be updated"`  
**Solution:** Session already started/completed. Create new session instead.

### Issue: Invalid Enum Value
**Error:** `400 BAD_REQUEST "Cannot deserialize value..."`  
**Solution:** Check enum values (e.g., use `VIDEO_CALL` not `video_call`)

---

## 📞 Support

All API endpoints are documented and ready to use. Files are located in:
- `/docs/telemedicine-api-examples.http`
- `/docs/telemedicine-quick-tests.http`
- `/docs/TELEMEDICINE_API_GUIDE.md`
- `/docs/telemedicine-samples.json`
- `/docs/SESSION_ID_FIX.md`

---

## ✅ Summary

- ✅ Session ID format corrected (TM-XXX)
- ✅ All HTTP files updated with correct format
- ✅ 42+ API examples ready to use
- ✅ 20 quick tests with your actual data
- ✅ Complete documentation provided
- ✅ All existing sessions documented

**Status:** Ready to test! 🚀

---

**Last Updated:** February 5, 2026  
**Total Sessions in DB:** 10  
**Files Created/Updated:** 5
