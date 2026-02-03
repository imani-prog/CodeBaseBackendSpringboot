package com.example.codebasebackend.Entities;

public enum SessionStatus {
    SCHEDULED,      // Session scheduled for future
    ACTIVE,         // Session currently in progress (live)
    PAUSED,         // Session temporarily paused
    COMPLETED,      // Session successfully completed
    CANCELLED,      // Session cancelled by patient/doctor
    TERMINATED,     // Session forcefully ended by admin
    NO_SHOW         // Patient didn't join scheduled session
}
