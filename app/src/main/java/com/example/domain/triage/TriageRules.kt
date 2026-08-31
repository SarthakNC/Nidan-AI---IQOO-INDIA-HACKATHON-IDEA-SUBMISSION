package com.example.domain.triage

/**
 * Isolated Clinical Rule Constants and Thresholds.
 * Designed for review and validation by qualified healthcare authorities (e.g. MoHFW / WHO guidelines).
 */
object TriageRules {
    // MUAC (Mid-Upper Arm Circumference) standard thresholds for children 6-59 months
    const val MUAC_SEVERE_MALNUTRITION_THRESHOLD_MM = 115 // < 11.5 cm -> RED
    const val MUAC_MODERATE_MALNUTRITION_THRESHOLD_MM = 125 // 11.5 - 12.5 cm -> YELLOW
    // >= 12.5 cm -> GREEN

    // High fever duration warning (days)
    const val HIGH_FEVER_CRITICAL_DAYS = 3
    const val PROLONGED_SYMPTOM_DAYS = 7

    // Anemia confidence minimum acceptable threshold for valid on-device screen
    const val MIN_ANEMIA_IMAGE_CONFIDENCE = 0.65f

    // Disclaimer text standard across all triage outputs
    const val CLINICAL_SAFETY_DISCLAIMER =
        "AI screening support only. Not a definitive medical diagnosis. Final clinical decisions must be confirmed by a qualified medical professional at a Primary Health Centre or Hospital."
}
