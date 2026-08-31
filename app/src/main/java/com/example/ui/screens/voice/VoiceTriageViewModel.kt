package com.example.ui.screens.voice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.reasoning.ClinicalReasoningEngine
import com.example.ai.reasoning.MockClinicalReasoningEngine
import com.example.ai.speech.MockSpeechRecognitionEngine
import com.example.ai.speech.SpeechRecognitionEngine
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class VoiceTriageState {
    IDLE,
    LISTENING,
    PROCESSING,
    RESULT
}

data class VoiceTriageUiState(
    val state: VoiceTriageState = VoiceTriageState.IDLE,
    val patient: Patient? = null,
    val transcript: String = "",
    val languageCode: String = "hi-IN", // "hi-IN", "en-IN", "mr-IN"
    val finding: ScreeningFinding? = null,
    val triageResult: TriageResult? = null,
    val workerEditableNotes: String = "",
    val isSaved: Boolean = false
)

class VoiceTriageViewModel(
    application: Application,
    private val patientId: String
) : AndroidViewModel(application) {

    private val db = NidanDatabase.getInstance(application)
    private val patientRepo = PatientRepository(db.patientDao())
    private val screeningRepo = ScreeningRepository(db.screeningDao())

    private val speechEngine: SpeechRecognitionEngine = MockSpeechRecognitionEngine()
    private val reasoningEngine: ClinicalReasoningEngine = MockClinicalReasoningEngine()
    private val triageEngine: TriageEngine = DeterministicTriageEngine()

    private val _uiState = MutableStateFlow(VoiceTriageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val p = patientRepo.getPatientById(patientId)
            _uiState.value = _uiState.value.copy(patient = p)
        }
    }

    fun setLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(languageCode = lang)
    }

    fun updateWorkerNotes(notes: String) {
        _uiState.value = _uiState.value.copy(workerEditableNotes = notes)
    }

    fun startListeningWithPreset(presetText: String? = null) {
        _uiState.value = _uiState.value.copy(state = VoiceTriageState.LISTENING)

        viewModelScope.launch {
            // Simulate voice capture duration
            delay(1500)

            _uiState.value = _uiState.value.copy(state = VoiceTriageState.PROCESSING)

            val text = presetText ?: when (_uiState.value.languageCode) {
                "hi-IN" -> "मरीज को तीन दिनों से तेज बुखार है और जोड़ों में तेज दर्द है, भूख भी नहीं लग रही है।"
                "mr-IN" -> "रुग्णाला ३ दिवसांपासून ताप आहे आणि अशक्तपणा जाणवत आहे."
                else -> "Patient has had high fever for three days, severe joint pain, and reduced appetite."
            }

            val finding = reasoningEngine.extractClinicalEntities(text)
            val triage = triageEngine.evaluate(finding, _uiState.value.patient?.age)

            _uiState.value = _uiState.value.copy(
                state = VoiceTriageState.RESULT,
                transcript = text,
                finding = finding,
                triageResult = triage,
                workerEditableNotes = "Audio screening: ${finding.symptoms.joinToString(", ")} for ${finding.durationDays ?: 3} days."
            )
        }
    }

    fun toggleListening() {
        if (_uiState.value.state == VoiceTriageState.IDLE) {
            startListeningWithPreset()
        } else if (_uiState.value.state == VoiceTriageState.RESULT) {
            _uiState.value = _uiState.value.copy(state = VoiceTriageState.IDLE)
        }
    }

    fun reset() {
        _uiState.value = _uiState.value.copy(state = VoiceTriageState.IDLE)
    }

    fun saveScreening(onSaved: () -> Unit) {
        val state = _uiState.value
        val triage = state.triageResult ?: return
        val p = state.patient

        val record = ScreeningRecord(
            patientId = patientId,
            patientName = p?.name ?: "Patient $patientId",
            patientAge = p?.age ?: 30,
            patientGender = p?.gender ?: "Female",
            screeningType = ScreeningType.VOICE_TRIAGE,
            triageLevel = triage.level,
            triageTitle = triage.title,
            recommendedAction = triage.recommendedAction,
            clinicalRationale = triage.clinicalExplanation,
            confidence = triage.confidence,
            findingsJson = "{\"transcript\":\"${state.transcript}\", \"symptoms\":${state.finding?.symptoms?.map { "\"$it\"" }}}",
            rawNotes = state.workerEditableNotes,
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
