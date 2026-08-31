package com.example.domain.triage

import com.example.domain.model.ScreeningFinding
import com.example.domain.model.TriageResult

/**
 * Core interface for clinical triage evaluation.
 * Decouples deterministic medical rule processing from UI and AI inference layers.
 */
interface TriageEngine {
    fun evaluate(finding: ScreeningFinding, patientAge: Int? = null): TriageResult
}
