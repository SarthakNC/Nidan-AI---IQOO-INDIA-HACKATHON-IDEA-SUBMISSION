package com.example.domain.model

data class Patient(
    val id: Long = 0,
    val patientId: String,
    val name: String,
    val age: Int,
    val gender: String,
    val villageArea: String,
    val abhaId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val latestTriageLevel: TriageLevel = TriageLevel.GREEN,
    val totalScreenings: Int = 0
)
