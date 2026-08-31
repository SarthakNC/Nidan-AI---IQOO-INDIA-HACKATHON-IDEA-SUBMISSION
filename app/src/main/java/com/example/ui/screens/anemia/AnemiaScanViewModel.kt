package com.example.ui.screens.anemia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.vision.MockVisionInferenceEngine
import com.example.ai.vision.VisionInferenceEngine
import com.example.data.local.NidanDatabase
import com.example.data.repository.PatientRepository
import com.example.data.repository.ScreeningRepository
import com.example.domain.model.Patient
import com.example.domain.model.ScreeningFinding
import com.example.domain.model.ScreeningRecord
import com.example.domain.model.ScreeningType
import com.example.domain.model.TriageLevel
import com.example.domain.model.TriageResult
import com.example.domain.triage.DeterministicTriageEngine
import com.example.domain.triage.TriageEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AnemiaScanStep {
    VIEWFINDER,
    ANALYZING,
    RESULT
}

data class AnemiaScanUiState(
    val step: AnemiaScanStep = AnemiaScanStep.VIEWFINDER,
    val patient: Patient? = null,
    val finding: ScreeningFinding? = null,
    val triageResult: TriageResult? = null,
    val simulatePallor: Boolean = true,
    val isSaved: Boolean = false
)

class AnemiaScanViewModel(
    application: Application,
    private val patientId: String
) : AndroidViewModel(application) {

    private val db = NidanDatabase.getInstance(application)
    private val patientRepo = PatientRepository(db.patientDao())
    private val screeningRepo = ScreeningRepository(db.screeningDao())

    private val visionEngine: VisionInferenceEngine = MockVisionInferenceEngine()
    private val triageEngine: TriageEngine = DeterministicTriageEngine()

    private val _uiState = MutableStateFlow(AnemiaScanUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val p = patientRepo.getPatientById(patientId)
            _uiState.value = _uiState.value.copy(patient = p)
        }
    }

    fun toggleSimulatedPallor(hasPallor: Boolean) {
        _uiState.value = _uiState.value.copy(simulatePallor = hasPallor)
    }

    fun captureAndAnalyze() {
        _uiState.value = _uiState.value.copy(step = AnemiaScanStep.ANALYZING)

        viewModelScope.launch {
            val finding = visionEngine.analyzeEyelidPallor()
            val adjustedFinding = finding.copy(
                conjunctivalPallorDetected = _uiState.value.simulatePallor,
                confidenceScore = if (_uiState.value.simulatePallor) 0.82f else 0.91f,
                observations = if (_uiState.value.simulatePallor) {
                    listOf(
                        "Visible conjunctival pallor (lower palpebral zone)",
                        "Erythema index: 0.38 (below baseline standard)",
                        "Image quality acceptable"
                    )
                } else {
                    listOf(
                        "Normal conjunctival vascularization",
                        "Erythema index: 0.56 (normal range)",
                        "Image quality acceptable"
                    )
                }
            )

            val triage = triageEngine.evaluate(adjustedFinding, _uiState.value.patient?.age)

            _uiState.value = _uiState.value.copy(
                step = AnemiaScanStep.RESULT,
                finding = adjustedFinding,
                triageResult = triage
            )
        }
    }

    fun retake() {
        _uiState.value = _uiState.value.copy(step = AnemiaScanStep.VIEWFINDER)
    }

    fun saveScreening(onSaved: () -> Unit) {
        val state = _uiState.value
        val triage = state.triageResult ?: return
        val p = state.patient

        val record = ScreeningRecord(
            patientId = patientId,
            patientName = p?.name ?: "Patient $patientId",
            patientAge = p?.age ?: 28,
            patientGender = p?.gender ?: "Female",
            screeningType = ScreeningType.ANEMIA,
            triageLevel = triage.level,
            triageTitle = triage.title,
            recommendedAction = triage.recommendedAction,
            clinicalRationale = triage.clinicalExplanation,
            confidence = triage.confidence,
            findingsJson = "{\"pallorDetected\":${state.finding?.conjunctivalPallorDetected}}",
            rawNotes = "Lower eyelid scan conducted offline on-device.",
            isSyncedWithPhc = false
        )

        viewModelScope.launch {
            screeningRepo.insertScreening(record)
            p?.let {
                patientRepo.updatePatient(it.copy(latestTriageLevel = triage.level, totalScreenings = it.totalScreenings + 1))
            }
            _uiState.value = state.copy(isSaved = true)
            onSaved()
        }
    }
}
