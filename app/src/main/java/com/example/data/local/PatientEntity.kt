package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val name: String,
    val age: Int,
    val gender: String,
    val villageArea: String,
    val abhaId: String = "",
    val latestTriageLevel: String = "GREEN",
    val totalScreenings: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
