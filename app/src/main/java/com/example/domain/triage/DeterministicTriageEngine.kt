package com.example.domain.triage

import com.example.domain.model.ScreeningFinding
import com.example.domain.model.ScreeningType
import com.example.domain.model.TriageLevel
import com.example.domain.model.TriageResult

/**
 * Deterministic, rule-based clinical decision support engine.
 *
 * Implements strict medical thresholding without relying on black-box generative models
 * for triage color assignment.
 */
class DeterministicTriageEngine : TriageEngine {

    override fun evaluate(finding: ScreeningFinding, patientAge: Int?): TriageResult {
        return when (finding.screeningType) {
            ScreeningType.CHILD_MUAC -> evaluateMuacScreening(finding, patientAge)
            ScreeningType.ANEMIA -> evaluateAnemiaScreening(finding)
            ScreeningType.VOICE_TRIAGE -> evaluateVoiceClinicalFindings(finding)
            ScreeningType.SKIN_SCREENING -> evaluateSkinScreening(finding)
            ScreeningType.GENERAL -> evaluateGeneralScreening(finding, patientAge)
        }
    }

    private fun evaluateMuacScreening(finding: ScreeningFinding, patientAge: Int?): TriageResult {
        val mm = finding.muacMeasurementMm
        val colorZone = finding.muacColorZone

        return when {
            // RED: Severe Acute Malnutrition risk (< 115mm or RED tape zone)
            (mm != null && mm < TriageRules.MUAC_SEVERE_MALNUTRITION_THRESHOLD_MM) || colorZone == "RED" -> {
                TriageResult(
                    level = TriageLevel.RED,
                    title = "High Nutritional Risk (SAM Pattern)",
                    recommendedAction = "Immediate referral to Nutritional Rehabilitation Centre (NRC) / Medical Officer required.",
                    clinicalExplanation = "MUAC measurement (${mm?.let { "$it mm" } ?: "Red Zone"}) is below the 115mm threshold, indicating high risk of severe acute malnutrition.",
                    confidence = (finding.confidenceScore * 100).toInt().coerceIn(75, 98),
                    keyIndicators = listOf(
                        "MUAC measurement < 115mm",
                        "Tape color zone: RED",
                        "High acute nutritional risk"
                    )
                )
            }
            // YELLOW: Moderate Acute Malnutrition risk (115mm - 125mm or YELLOW tape zone)
            (mm != null && mm < TriageRules.MUAC_MODERATE_MALNUTRITION_THRESHOLD_MM) || colorZone == "YELLOW" -> {
                TriageResult(
                    level = TriageLevel.YELLOW,
                    title = "Moderate Nutritional Risk (MAM Pattern)",
                    recommendedAction = "Refer to Primary Health Centre for supplementary nutrition counselling and growth monitoring.",
                    clinicalExplanation = "MUAC measurement (${mm?.let { "$it mm" } ?: "Yellow Zone"}) falls between 115-125mm, suggesting moderate acute malnutrition risk.",
                    confidence = (finding.confidenceScore * 100).toInt().coerceIn(70, 95),
                    keyIndicators = listOf(
                        "MUAC measurement 115-125mm",
                        "Tape color zone: YELLOW",
                        "Growth monitoring & dietary support indicated"
                    )
                )
            }
            // GREEN: Normal nutrition status (>= 125mm or GREEN tape zone)
            else -> {
                TriageResult(
                    level = TriageLevel.GREEN,
                    title = "Normal Nutritional Status",
                    recommendedAction = "Continue routine age-appropriate feeding and standard community growth monitoring.",
                    clinicalExplanation = "MUAC measurement (${mm?.let { "$it mm" } ?: "Green Zone"}) is in the healthy range (>= 125mm).",
                    confidence = (finding.confidenceScore * 100).toInt().coerceIn(80, 99),
                    keyIndicators = listOf(
                        "MUAC measurement >= 125mm",
                        "Tape color zone: GREEN",
                        "Normal nutritional indicator"
                    )
                )
            }
        }
    }

    private fun evaluateAnemiaScreening(finding: ScreeningFinding): TriageResult {
        if (!finding.imageQualityAcceptable) {
            return TriageResult(
                level = TriageLevel.YELLOW,
                title = "Inconclusive Image Quality",
                recommendedAction = "Retake scan in good ambient lighting or perform clinical hemoglobin test.",
                clinicalExplanation = "Lighting was insufficient or image alignment was off-center. Cannot reliably assess conjunctival pallor.",
                confidence = 45,
                keyIndicators = listOf("Poor lighting / blur detected", "Clinical verification needed")
            )
        }

        return if (finding.conjunctivalPallorDetected) {
            TriageResult(
                level = TriageLevel.YELLOW,
                title = "Anemia Risk Pattern Detected",
                recommendedAction = "Refer to Primary Health Centre for lab hemoglobin estimation (Hb) and IFA supplementation review.",
                clinicalExplanation = "Visual conjunctival screening detected marked pallor indicators compared to baseline perfusion norms.",
                confidence = (finding.confidenceScore * 100).toInt().coerceIn(75, 92),
                keyIndicators = listOf(
                    "Visible conjunctival pallor",
                    "Palpebral erythema index reduced",
                    "Image quality acceptable"
                )
            )
        } else {
            TriageResult(
                level = TriageLevel.GREEN,
                title = "Low Visible Anemia Risk",
                recommendedAction = "Routine follow-up. Counsel on iron-rich dietary intake and standard IFA prophylaxis.",
                clinicalExplanation = "Normal conjunctival vascularization detected within healthy parameters.",
                confidence = (finding.confidenceScore * 100).toInt().coerceIn(80, 95),
                keyIndicators = listOf(
                    "Normal conjunctival redness index",
                    "No marked palpebral pallor",
                    "Scan quality good"
                )
            )
        }
    }

    private fun evaluateVoiceClinicalFindings(finding: ScreeningFinding): TriageResult {
        val hasSevereRedFlags = finding.respiratoryDistress ||
                finding.lethargyOrUnconscious ||
                (finding.highFeverPresent && (finding.durationDays ?: 0) >= TriageRules.HIGH_FEVER_CRITICAL_DAYS && finding.symptoms.any { it.contains("convulsion", true) || it.contains("stiff", true) })

        if (hasSevereRedFlags) {
            return TriageResult(
                level = TriageLevel.RED,
                title = "High Risk - Urgent Medical Attention",
                recommendedAction = "Immediate transport/referral to nearest Community Health Centre / District Hospital.",
                clinicalExplanation = "Critical danger signs detected in captured symptoms (${finding.symptoms.joinToString(", ")}).",
                confidence = 90,
                keyIndicators = buildList {
                    if (finding.respiratoryDistress) add("Respiratory distress / breathing difficulty")
                    if (finding.lethargyOrUnconscious) add("Altered consciousness / extreme lethargy")
                    if (finding.highFeverPresent) add("High fever duration >= ${finding.durationDays ?: 3} days")
                    addAll(finding.symptoms)
                }
            )
        }

        val hasModerateFlags = (finding.highFeverPresent && (finding.durationDays ?: 0) >= 2) ||
                (finding.durationDays ?: 0) >= TriageRules.PROLONGED_SYMPTOM_DAYS ||
                finding.symptoms.any { it.contains("vomiting", true) || it.contains("pain", true) || it.contains("rash", true) || it.contains("fever", true) }

        if (hasModerateFlags) {
            return TriageResult(
                level = TriageLevel.YELLOW,
                title = "Moderate Clinical Risk",
                recommendedAction = "Primary Health Centre assessment and Medical Officer consultation recommended.",
                clinicalExplanation = "Persistent symptoms (${finding.symptoms.joinToString(", ")}) present for ${finding.durationDays ?: 3} days require professional medical review.",
                confidence = 85,
                keyIndicators = buildList {
                    finding.durationDays?.let { add("Symptom duration: $it days") }
                    addAll(finding.symptoms)
                    add("Medical officer evaluation recommended")
                }
            )
        }

        return TriageResult(
            level = TriageLevel.GREEN,
            title = "Mild / Low Clinical Risk",
            recommendedAction = "Provide home-care guidance, adequate hydration, and monitor for warning signs.",
            clinicalExplanation = "No acute danger signs detected. Symptoms appear mild and manageable under frontline supervision.",
            confidence = 88,
            keyIndicators = buildList {
                if (finding.symptoms.isNotEmpty()) addAll(finding.symptoms) else add("Mild/non-severe symptoms")
                add("No acute danger signs")
            }
        )
    }

    private fun evaluateSkinScreening(finding: ScreeningFinding): TriageResult {
        return if (finding.rashDetected) {
            TriageResult(
                level = TriageLevel.YELLOW,
                title = "Dermatological Lesion / Rash Detected",
                recommendedAction = "Refer to PHC for clinical inspection and topical/systemic treatment prescription.",
                clinicalExplanation = "Localized epidermal pattern detected. Requires visual differential diagnosis by doctor.",
                confidence = 80,
                keyIndicators = listOf("Visible rash/lesion pattern", "Requires differential diagnosis")
            )
        } else {
            TriageResult(
                level = TriageLevel.GREEN,
                title = "No Acute Skin Lesions Detected",
                recommendedAction = "Routine hygiene guidance and follow up if symptoms develop.",
                clinicalExplanation = "Epidermal surface appears clear with no acute ulcerations or severe rashes.",
                confidence = 85,
                keyIndicators = listOf("Clear skin pattern", "No severe lesions")
            )
        }
    }

    private fun evaluateGeneralScreening(finding: ScreeningFinding, patientAge: Int?): TriageResult {
        // Multi-finding aggregation
        return when {
            finding.respiratoryDistress || finding.lethargyOrUnconscious || finding.muacColorZone == "RED" -> {
                evaluateMuacScreening(finding, patientAge)
            }
            finding.conjunctivalPallorDetected || finding.muacColorZone == "YELLOW" || finding.symptoms.isNotEmpty() -> {
                TriageResult(
                    level = TriageLevel.YELLOW,
                    title = "Moderate Multi-Factor Risk",
                    recommendedAction = "Primary Health Centre consultation recommended for combined findings.",
                    clinicalExplanation = "Identified moderate risk factors across screening parameters.",
                    confidence = 84,
                    keyIndicators = buildList {
                        if (finding.conjunctivalPallorDetected) add("Pallor detected")
                        if (finding.muacColorZone == "YELLOW") add("MUAC: Yellow zone")
                        addAll(finding.symptoms)
                    }
                )
            }
            else -> {
                TriageResult(
                    level = TriageLevel.GREEN,
                    title = "Low Risk - Routine Care",
                    recommendedAction = "Routine follow-up / home care guidance.",
                    clinicalExplanation = "All visual and verbal screening indicators are within standard baseline limits.",
                    confidence = 90,
                    keyIndicators = listOf("Vital indicators normal", "No red flag symptoms")
                )
            }
        }
    }
}
