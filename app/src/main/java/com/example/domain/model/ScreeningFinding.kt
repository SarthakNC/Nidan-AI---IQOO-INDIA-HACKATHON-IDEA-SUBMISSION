package com.example.domain.model

data class ScreeningFinding(
    val screeningType: ScreeningType,
    val conjunctivalPallorDetected: Boolean = false,
    val muacMeasurementMm: Int? = null,
    val muacColorZone: String? = null, // "GREEN", "YELLOW", "RED"
    val symptoms: List<String> = emptyList(),
    val durationDays: Int? = null,
    val highFeverPresent: Boolean = false,
    val respiratoryDistress: Boolean = false,
    val lethargyOrUnconscious: Boolean = false,
    val rashDetected: Boolean = false,
    val rawTranscript: String = "",
    val confidenceScore: Float = 0.85f,
    val imageQualityAcceptable: Boolean = true,
    val observations: List<String> = emptyList(),
    val workerNotes: String = ""
)
