package com.example.ui.screens.newpatient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NidanDatabase
import com.example.data.repository.PatientRepository
import com.example.domain.model.Patient
import com.example.domain.model.ScreeningType
import com.example.domain.model.TriageLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class NewPatientUiState(
    val currentStep: Int = 1,
    val patientId: String = "P-${Random.nextInt(1000, 9999)}",
    val name: String = "",
    val age: String = "",
    val gender: String = "Female",
    val villageArea: String = "Kalyanpur Ward 4",
    val abhaId: String = "",
    val selectedScreeningType: ScreeningType = ScreeningType.CHILD_MUAC,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

class NewPatientViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NidanDatabase.getInstance(application)
    private val patientRepo = PatientRepository(db.patientDao())

    private val _uiState = MutableStateFlow(NewPatientUiState())
    val uiState = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun updateAge(age: String) {
        _uiState.value = _uiState.value.copy(age = age, errorMessage = null)
    }

    fun updateGender(gender: String) {
        _uiState.value = _uiState.value.copy(gender = gender)
    }

    fun updateVillage(village: String) {
        _uiState.value = _uiState.value.copy(villageArea = village)
    }

    fun updateAbhaId(abha: String) {
        _uiState.value = _uiState.value.copy(abhaId = abha)
    }

    fun selectScreeningType(type: ScreeningType) {
        _uiState.value = _uiState.value.copy(selectedScreeningType = type)
    }

    fun prefillDemoPatient(name: String, age: Int, gender: String, village: String, abha: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            age = age.toString(),
            gender = gender,
            villageArea = village,
            abhaId = abha,
            errorMessage = null
        )
    }

    fun proceedToStep2(): Boolean {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Please enter patient name.")
            return false
        }
        val ageInt = state.age.toIntOrNull()
        if (ageInt == null || ageInt <= 0 || ageInt > 120) {
            _uiState.value = state.copy(errorMessage = "Please enter a valid age.")
            return false
        }

        _uiState.value = state.copy(currentStep = 2, errorMessage = null)
        return true
    }

    fun goToStep1() {
        _uiState.value = _uiState.value.copy(currentStep = 1)
    }

    fun saveAndStartScreening(onComplete: (patientId: String, screeningType: ScreeningType) -> Unit) {
        val state = _uiState.value
        val ageInt = state.age.toIntOrNull() ?: 7
        val patient = Patient(
            patientId = state.patientId,
            name = state.name.trim(),
            age = ageInt,
            gender = state.gender,
            villageArea = state.villageArea.trim(),
            abhaId = state.abhaId.trim(),
            latestTriageLevel = TriageLevel.GREEN
        )

        viewModelScope.launch {
            patientRepo.insertPatient(patient)
            _uiState.value = state.copy(isSaved = true)
            onComplete(patient.patientId, state.selectedScreeningType)
        }
    }
}
