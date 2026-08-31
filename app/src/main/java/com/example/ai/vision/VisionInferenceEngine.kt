package com.example.ai.vision

import com.example.domain.model.ScreeningFinding
import com.example.domain.model.ScreeningType
import kotlinx.coroutines.delay

interface VisionInferenceEngine {
    suspend fun analyzeEyelidPallor(imageByteArray: ByteArray? = null): ScreeningFinding
    suspend fun analyzeMuacTape(imageByteArray: ByteArray? = null, presetZone: String? = null): ScreeningFinding
}

/**
 * Realistic local vision engine simulation representing on-device MobileNet/YOLO INT8 models.
 * Operates completely offline with deterministic mock variations for demo/field testing.
 */
class MockVisionInferenceEngine : VisionInferenceEngine {

    override suspend fun analyzeEyelidPallor(imageByteArray: ByteArray?): ScreeningFinding {
        // Simulate local INT8 NPU inference delay (~350ms)
        delay(400)

        return ScreeningFinding(
            screeningType = ScreeningType.ANEMIA,
            conjunctivalPallorDetected = true,
            confidenceScore = 0.82f,
            imageQualityAcceptable = true,
            observations = listOf(
                "Visible conjunctival pallor (lower palpebral zone)",
                "Erythema index: 0.38 (below standard 0.52 norm)",
                "Ambient lighting: Adequate (340 lux)"
            ),
            workerNotes = "Patient shows visible paleness in inner eyelid membrane."
        )
    }

    override suspend fun analyzeMuacTape(imageByteArray: ByteArray?, presetZone: String?): ScreeningFinding {
        delay(450)

        val zone = presetZone ?: "RED"
        val (measurement, pallor, obs) = when (zone) {
            "RED" -> Triple(
                110,
                listOf(
                    "Detected Tape Color Band: RED",
                    "Segmented circumference: 11.0 cm (110 mm)",
                    "Severe acute malnutrition indicator"
                ),
                0.94f
            )
            "YELLOW" -> Triple(
                121,
                listOf(
                    "Detected Tape Color Band: YELLOW",
                    "Segmented circumference: 12.1 cm (121 mm)",
                    "Moderate acute malnutrition indicator"
                ),
                0.88f
            )
            else -> Triple(
                134,
                listOf(
                    "Detected Tape Color Band: GREEN",
                    "Segmented circumference: 13.4 cm (134 mm)",
                    "Normal nutritional threshold"
                ),
                0.96f
            )
        }

        return ScreeningFinding(
            screeningType = ScreeningType.CHILD_MUAC,
            muacMeasurementMm = measurement,
            muacColorZone = zone,
            confidenceScore = obs,
            imageQualityAcceptable = true,
            observations = pallor,
            workerNotes = "Tape placed snugly around left mid-upper arm."
        )
    }
}
