package com.example.ui.screens.patients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NidanDatabase
import com.example.data.repository.PatientRepository
import com.example.data.repository.ScreeningRepository
import com.example.domain.model.Patient
import com.example.domain.model.ScreeningRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PatientsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NidanDatabase.getInstance(application)
    private val patientRepo = PatientRepository(db.patientDao())
    private val screeningRepo = ScreeningRepository(db.screeningDao())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val patients: StateFlow<List<Patient>> = _searchQuery
        .flatMapLatest { q ->
            if (q.isBlank()) patientRepo.allPatients else patientRepo.searchPatients(q)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    suspend fun getPatient(patientId: String): Patient? {
        return patientRepo.getPatientById(patientId)
    }

    fun getScreeningsForPatient(patientId: String): StateFlow<List<ScreeningRecord>> {
        return screeningRepo.getScreeningsForPatient(patientId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
}
