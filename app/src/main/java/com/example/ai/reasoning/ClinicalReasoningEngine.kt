package com.example.ai.reasoning

import com.example.domain.model.ScreeningFinding
import com.example.domain.model.ScreeningType
import kotlinx.coroutines.delay

interface ClinicalReasoningEngine {
    suspend fun extractClinicalEntities(transcript: String): ScreeningFinding
}

/**
 * Offline Clinical Reasoning abstraction for entity extraction from unstructured speech notes.
 * Extracts symptoms, duration, and key flags safely for the deterministic triage engine.
 */
class MockClinicalReasoningEngine : ClinicalReasoningEngine {

    override suspend fun extractClinicalEntities(transcript: String): ScreeningFinding {
        delay(350)

        val lower = transcript.lowercase()
        val symptoms = mutableListOf<String>()
        var duration = 3
        var highFever = false
        var respDistress = false
        var lethargy = false

        if (lower.contains("fever") || lower.contains("बुखार") || lower.contains("ताप")) {
            symptoms.add("Fever")
            highFever = true
        }
        if (lower.contains("joint") || lower.contains("दर्द") || lower.contains("body ache")) {
            symptoms.add("Joint Pain")
        }
        if (lower.contains("appetite") || lower.contains("भूख") || lower.contains("food")) {
            symptoms.add("Reduced Appetite")
        }
        if (lower.contains("breath") || lower.contains("सांस") || lower.contains("cough")) {
            symptoms.add("Difficulty Breathing")
            respDistress = true
        }
        if (lower.contains("weak") || lower.contains("अशक्तपणा") || lower.contains("tired") || lower.contains("letharg")) {
            symptoms.add("Marked Weakness / Lethargy")
            lethargy = true
        }

        if (symptoms.isEmpty()) {
            symptoms.add("Fever")
            symptoms.add("Joint Pain")
        }

        // Duration detection heuristic
        if (lower.contains("3") || lower.contains("तीन") || lower.contains("three")) duration = 3
        if (lower.contains("5") || lower.contains("पांच") || lower.contains("five")) duration = 5
        if (lower.contains("7") || lower.contains("सात") || lower.contains("week")) duration = 7

        return ScreeningFinding(
            screeningType = ScreeningType.VOICE_TRIAGE,
            symptoms = symptoms,
            durationDays = duration,
            highFeverPresent = highFever,
            respiratoryDistress = respDistress,
            lethargyOrUnconscious = lethargy,
            rawTranscript = transcript,
            confidenceScore = 0.91f,
            observations = listOf(
                "Extracted ${symptoms.size} primary clinical entities",
                "Duration parsed: $duration days",
                "Zero cloud transmission (On-device SLM parser)"
            )
        )
    }
}
