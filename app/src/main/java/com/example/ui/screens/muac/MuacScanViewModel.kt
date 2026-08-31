package com.example.ui.screens.muac

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

enum class MuacScanStep {
    VIEWFINDER,
    ANALYZING,
    RESULT
}

data class MuacScanUiState(
    val step: MuacScanStep = MuacScanStep.VIEWFINDER,
    val patient: Patient? = null,
    val finding: ScreeningFinding? = null,
    val triageResult: TriageResult? = null,
    val selectedSimulatedZone: String = "RED",
    val isSaved: Boolean = false
)

class MuacScanViewModel(
    application: Application,
    private val patientId: String
) : AndroidViewModel(application) {

    private val db = NidanDatabase.getInstance(application)
    private val patientRepo = PatientRepository(db.patientDao())
    private val screeningRepo = ScreeningRepository(db.screeningDao())

    private val visionEngine: VisionInferenceEngine = MockVisionInferenceEngine()
    private val triageEngine: TriageEngine = DeterministicTriageEngine()

    private val _uiState = MutableStateFlow(MuacScanUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val p = patientRepo.getPatientById(patientId)
            _uiState.value = _uiState.value.copy(patient = p)
        }
    }

    fun selectSimulatedZone(zone: String) {
        _uiState.value = _uiState.value.copy(selectedSimulatedZone = zone)
    }

    fun captureAndAnalyze() {
        _uiState.value = _uiState.value.copy(step = MuacScanStep.ANALYZING)

        viewModelScope.launch {
            val finding = visionEngine.analyzeMuacTape(presetZone = _uiState.value.selectedSimulatedZone)
            val triage = triageEngine.evaluate(finding, _uiState.value.patient?.age)

            _uiState.value = _uiState.value.copy(
                step = MuacScanStep.RESULT,
                finding = finding,
                triageResult = triage
            )
        }
    }

    fun retake() {
        _uiState.value = _uiState.value.copy(step = MuacScanStep.VIEWFINDER)
    }

    fun saveScreening(onSaved: () -> Unit) {
        val state = _uiState.value
        val triage = state.triageResult ?: return
        val p = state.patient

        val record = ScreeningRecord(
            patientId = patientId,
            patientName = p?.name ?: "Patient $patientId",
            patientAge = p?.age ?: 4,
            patientGender = p?.gender ?: "Male",
            screeningType = ScreeningType.CHILD_MUAC,
            triageLevel = triage.level,
            triageTitle = triage.title,
            recommendedAction = triage.recommendedAction,
            clinicalRationale = triage.clinicalExplanation,
            confidence = triage.confidence,
            findingsJson = "{\"muacMm\":${state.finding?.muacMeasurementMm}, \"zone\":\"${state.finding?.muacColorZone}\"}",
            rawNotes = "Child MUAC arm circumference scanned via local vision model.",
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
