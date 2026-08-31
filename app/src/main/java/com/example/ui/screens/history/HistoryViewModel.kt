package com.example.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NidanDatabase
import com.example.data.repository.ScreeningRepository
import com.example.domain.model.ScreeningRecord
import com.example.domain.model.TriageLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NidanDatabase.getInstance(application)
    private val screeningRepo = ScreeningRepository(db.screeningDao())

    private val _selectedFilter = MutableStateFlow<TriageLevel?>(null)
    val selectedFilter = _selectedFilter.asStateFlow()

    val screenings: StateFlow<List<ScreeningRecord>> = _selectedFilter
        .flatMapLatest { filter ->
            if (filter == null) {
                screeningRepo.allScreenings
            } else {
                screeningRepo.getScreeningsByLevel(filter)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setFilter(filter: TriageLevel?) {
        _selectedFilter.value = filter
    }
}
