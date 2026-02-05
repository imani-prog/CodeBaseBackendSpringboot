# Telemedicine API Documentation

This folder contains complete documentation and testing resources for the Telemedicine Session API.

## 📚 Documentation Files

### 1. **TESTING_GUIDE_COMPLETE.md** ⭐ START HERE
**Purpose:** Complete guide to using the Telemedicine API  
**Contains:**
- Quick start instructions
- Your current session data (TM-001 through TM-010)
- 10 ready-to-run test examples
- Session lifecycle workflows
- Best practices and troubleshooting

**Read this first for a comprehensive overview!**

### 2. **SESSION_ID_FIX.md**
**Purpose:** Explanation of the session ID format issue and fix  
**Contains:**
- Problem description (TELE-2026-00001 vs TM-001)
- Root cause analysis
- Table of your existing sessions
- Fixed files list

**Read this to understand what was wrong and how it was fixed.**

### 3. **TELEMEDICINE_API_GUIDE.md**
**Purpose:** Complete API reference documentation  
**Contains:**
- All endpoint descriptions
- Request/response examples
- Enum values
- Error handling
- Common use cases

**Use this as your API reference manual.**

## 🧪 Testing Files

### 4. **telemedicine-quick-tests.http** ⭐ QUICK TESTING
**Purpose:** 20 quick tests using your actual data  
**How to use:**
1. Open in IntelliJ IDEA
2. Click green "play" button next to any request
3. View results in HTTP Client panel

**Perfect for:**
- Quick verification that endpoints work
- Testing with existing sessions (TM-001 to TM-010)
- Rapid debugging

### 5. **telemedicine-api-examples.http** ⭐ COMPREHENSIVE EXAMPLES
**Purpose:** 42+ complete API request examples  
**How to use:**
1. Open in IntelliJ IDEA
2. Execute any request with the play button
3. Modify as needed for your testing

**Perfect for:**
- Creating new sessions
- Testing all CRUD operations
- Session state management (start, pause, complete)
- Advanced filtering and search
- Analytics and reporting

### 6. **telemedicine-samples.json**
**Purpose:** Copy-paste ready JSON payloads  
**Contains:**
- 12 different session creation scenarios
- Complete test data (doctors, patients, hospitals)
- Enum value references
- Common workflow examples

**Perfect for:**
- Copying JSON payloads for Postman
- Reference for required fields
- Understanding data structure

## 🎯 Quick Start

### For First-Time Testing
1. Read **TESTING_GUIDE_COMPLETE.md** (5 minutes)
2. Open **telemedicine-quick-tests.http** in IntelliJ
3. Run Test 1: `GET .../by-session-id/TM-001`
4. Success! ✅

### For Creating New Sessions
1. Open **telemedicine-api-examples.http**
2. Find request #1 (CREATE SESSION)
3. Modify patient/doctor IDs as needed
4. Click play button
5. Note the returned session ID (e.g., TM-011)

### For API Reference
1. Open **TELEMEDICINE_API_GUIDE.md**
2. Find your endpoint
3. Copy the example
4. Test in HTTP file

## 📊 Your Current Data

### Sessions (10 total)
```
TM-001: Diabetes consultation (Patient 4 → Dr. 11)
TM-002: Emergency chest pain (Patient 5 → Dr. 11)
TM-003: Skin rash (Patient 6 → Dr. 16)
TM-004: Mental health (Patient 7 → Dr. 15)
TM-005: Prescription renewal (Patient 8 → Dr. 11)
TM-006-008: Asthma follow-ups (Patient 6 → Dr. 10)
TM-009: Gastroenterology (Patient 4 → Dr. 18)
TM-010: Orthopedic (Patient 7 → Dr. 13)
```

### Doctors (11 available)
- ID 2: Dr. John Mwangi (Pediatric Surgeon)
- ID 10: Dr. Emily Carter (Pediatrician)
- ID 11: Dr. Michael Ochieng (Cardiologist)
- ID 12: Dr. Sarah Kamau (Neurologist)
- ID 13: Dr. David Mwangi (Orthopedic Surgeon)
- ID 14: Dr. Grace Wanjiku (Bariatric Surgeon)
- ID 15: Dr. James Otieno (Psychiatrist)
- ID 16: Dr. Linda Njeri (Dermatologist)
- ID 17: Dr. Robert Kariuki (Pediatric Surgeon)
- ID 18: Dr. Patricia Mutua (Gastroenterologist)
- ID 19: Dr. Daniel Kipchoge (Neurosurgeon)

### Patients (5 available)
- ID 4: John Mwangi (Male, Diabetes, Nairobi)
- ID 5: Aisha Ali (Female, Hypertension, Mombasa)
- ID 6: Otieno Ouma (Male, Asthma, Kisumu)
- ID 7: Chebet Koech (Female, Asthma, Eldoret)
- ID 8: Wanjiru Kamau (Female, Hypertension+Diabetes, Nakuru)

## 🔧 File Organization

```
docs/
├── README.md (this file)
├── TESTING_GUIDE_COMPLETE.md ⭐ Start here
├── SESSION_ID_FIX.md
├── TELEMEDICINE_API_GUIDE.md
├── telemedicine-quick-tests.http ⭐ Quick tests
├── telemedicine-api-examples.http ⭐ All examples
└── telemedicine-samples.json
```

## 💡 Tips

### Using HTTP Files in IntelliJ
1. HTTP files have syntax highlighting
2. Click green arrow/play button to execute
3. Responses appear in side panel
4. Can save responses to files
5. Can use environment variables

### Session ID Format
- ✅ Correct: `TM-001`, `TM-002`, `TM-010`
- ❌ Wrong: `TELE-2026-00001`
- Format: `TM-XXX` (3-digit zero-padded)

### Common Endpoints
```
GET    /api/telemedicine/sessions           # List all
GET    /api/telemedicine/sessions/1         # Get by ID
GET    /api/telemedicine/sessions/by-session-id/TM-001  # Get by session ID
POST   /api/telemedicine/sessions           # Create new
PUT    /api/telemedicine/sessions/1         # Update
DELETE /api/telemedicine/sessions/1         # Delete
POST   /api/telemedicine/sessions/1/start   # Start session
POST   /api/telemedicine/sessions/1/complete  # Complete session
```

## 🎓 Learning Path

### Beginner
1. Read **TESTING_GUIDE_COMPLETE.md**
2. Run tests in **telemedicine-quick-tests.http**
3. Try creating a new session

### Intermediate
1. Explore all examples in **telemedicine-api-examples.http**
2. Test session lifecycle (create → start → complete → rate)
3. Try filtering and search endpoints

### Advanced
1. Study **TELEMEDICINE_API_GUIDE.md** thoroughly
2. Create complex filter queries
3. Test error scenarios
4. Integrate with your frontend

## ✅ Verification Checklist

Use this to verify everything works:

- [ ] Can get session by ID: `GET /api/telemedicine/sessions/1`
- [ ] Can get session by session ID: `GET .../by-session-id/TM-001`
- [ ] Can create new session: `POST /api/telemedicine/sessions`
- [ ] Can start session: `POST .../1/start`
- [ ] Can complete session: `POST .../1/complete`
- [ ] Can search sessions: `GET .../search?term=diabetes`
- [ ] Can filter by patient: `GET .../by-patient/4`
- [ ] Can filter by doctor: `GET .../by-doctor/11`
- [ ] Can get overview: `GET .../overview`
- [ ] Can rate session: `POST .../1/rate?rating=5`

## 🐛 Troubleshooting

### "Session not found with ID: TELE-2026-00001"
**Solution:** Use correct format: `TM-001` instead

### "Doctor is currently unavailable"
**Solution:** Doctor has max concurrent sessions. Use different doctor.

### "Only scheduled sessions can be updated"
**Solution:** Can't update sessions that have started. Create new one.

### Request doesn't work
**Solution:** Check that Spring Boot app is running on `localhost:8080`

## 📞 Need Help?

1. Check **TESTING_GUIDE_COMPLETE.md** troubleshooting section
2. Review **TELEMEDICINE_API_GUIDE.md** error handling
3. Verify request format in **telemedicine-api-examples.http**
4. Check JSON syntax in **telemedicine-samples.json**

## 🚀 Status

✅ All files created and verified  
✅ Session ID format corrected  
✅ 42+ API examples ready  
✅ 20 quick tests available  
✅ Complete documentation provided  
✅ Your 10 existing sessions documented  

**Everything is ready to use!**

---

**Last Updated:** February 5, 2026  
**Total Files:** 7  
**Total API Examples:** 42+  
**Quick Tests:** 20
