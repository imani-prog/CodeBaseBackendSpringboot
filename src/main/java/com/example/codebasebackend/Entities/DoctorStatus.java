package com.example.codebasebackend.Entities;

public enum DoctorStatus {
    AVAILABLE,   // Ready to take sessions
    BUSY,        // Currently in a session
    OFFLINE,     // Not available
    BREAK        // On scheduled break
}
