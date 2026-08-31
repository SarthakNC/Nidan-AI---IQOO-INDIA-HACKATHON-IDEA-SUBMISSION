package com.example.domain.model

data class TriageResult(
    val level: TriageLevel,
    val title: String,
    val recommendedAction: String,
    val clinicalExplanation: String,
    val confidence: Int = 85,
    val keyIndicators: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)
