package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenings")
data class ScreeningEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val patientName: String,
    val patientAge: Int,
    val patientGender: String,
    val screeningType: String,
    val triageLevel: String,
    val triageTitle: String,
    val recommendedAction: String,
    val clinicalRationale: String,
    val confidence: Int,
    val findingsJson: String,
    val rawNotes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSyncedWithPhc: Boolean = false
)
