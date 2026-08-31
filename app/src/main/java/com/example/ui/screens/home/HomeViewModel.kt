package com.example.ui.screens.home

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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val recentScreenings: List<ScreeningRecord> = emptyList(),
    val todayScreeningsCount: Int = 12,
    val pendingSyncCount: Int = 4,
    val totalPatientsCount: Int = 5,
    val isLoading: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NidanDatabase.getInstance(application)
    private val patientRepo = PatientRepository(db.patientDao())
    private val screeningRepo = ScreeningRepository(db.screeningDao())

    val recentScreenings: StateFlow<List<ScreeningRecord>> = screeningRepo.allScreenings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todayCount: StateFlow<Int> = screeningRepo.getTodayCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 12
        )

    val pendingSync: StateFlow<Int> = screeningRepo.pendingSyncCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 4
        )

    init {
        viewModelScope.launch {
            patientRepo.populateInitialDataIfEmpty()
            screeningRepo.populateInitialDataIfEmpty()
        }
    }
}
