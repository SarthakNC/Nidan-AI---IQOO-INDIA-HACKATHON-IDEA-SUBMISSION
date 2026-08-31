package com.example.domain.model

data class ScreeningRecord(
    val id: Long = 0,
    val patientId: String,
    val patientName: String,
    val patientAge: Int,
    val patientGender: String,
    val screeningType: ScreeningType,
    val triageLevel: TriageLevel,
    val triageTitle: String,
    val recommendedAction: String,
    val clinicalRationale: String,
    val confidence: Int,
    val findingsJson: String, // structured details
    val rawNotes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSyncedWithPhc: Boolean = false
)
