package com.example.ui.screens.handover

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NidanDatabase
import com.example.data.repository.ScreeningRepository
import com.example.officekit.ConnectionStatus
import com.example.officekit.MockOfficeKitBridge
import com.example.officekit.OfficeKitBridge
import com.example.officekit.OfficeKitState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HandoverViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NidanDatabase.getInstance(application)
    private val screeningRepo = ScreeningRepository(db.screeningDao())

    private val bridge: OfficeKitBridge = MockOfficeKitBridge()
    val officeKitState: StateFlow<OfficeKitState> = bridge.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OfficeKitState()
        )

    val pendingCount: StateFlow<Int> = screeningRepo.pendingSyncCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 4
        )

    fun connectToWorkstation() {
        viewModelScope.launch {
            bridge.searchAndConnect()
        }
    }

    fun startTransfer() {
        viewModelScope.launch {
            val count = pendingCount.value
            val success = bridge.transferPendingRecords(count)
            if (success) {
                screeningRepo.markAllAsSynced()
            }
        }
    }

    fun toggleScreenMirror() {
        bridge.toggleScreenMirroring()
    }

    fun disconnect() {
        bridge.disconnect()
    }
}
