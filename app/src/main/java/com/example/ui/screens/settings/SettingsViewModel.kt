package com.example.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NidanDatabase
import com.example.data.repository.DemoDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val selectedLanguage: String = "हिन्दी (Hindi)",
    val isHardwareNpuActive: Boolean = true,
    val isEncryptedStorageActive: Boolean = true,
    val isResetting: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NidanDatabase.getInstance(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun setLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(selectedLanguage = lang)
    }

    fun toggleNpu(active: Boolean) {
        _uiState.value = _uiState.value.copy(isHardwareNpuActive = active)
    }

    fun resetDemoData(onResetComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isResetting = true)
            db.clearAllTables()
            db.patientDao().insertPatients(DemoDataProvider.getInitialPatients())
            db.screeningDao().insertScreenings(DemoDataProvider.getInitialScreenings())
            _uiState.value = _uiState.value.copy(isResetting = false)
            onResetComplete()
        }
    }
}
