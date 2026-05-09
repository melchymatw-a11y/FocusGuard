package com.example.focusguard.models

data class Partner(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val status: String = "pending",
    val since: Long = 0L
)

data class PartnerRequest(
    val fromUid: String = "",
    val fromEmail: String = "",
    val fromName: String = "",
    val toEmail: String = "",
    val timestamp: Long = 0L,
    val status: String = "pending"
)

data class PartnerProgress(
    val partnerId: String = "",
    val partnerEmail: String = "",
    val totalGoals: Int = 0,
    val completedSessions: Int = 0,
    val totalFocusMinutes: Long = 0L,
    val lastActive: Long = 0L
)