package com.example.officekit

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ConnectionStatus {
    DISCONNECTED,
    SEARCHING_WORKSTATION,
    CONNECTED_DOCTOR_PC,
    TRANSFERRING,
    TRANSFER_COMPLETED,
    ERROR
}

data class OfficeKitState(
    val status: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val workstationName: String = "Dr. Mehta's PC (PHC-Room-2)",
    val workstationIp: String = "192.168.43.102:8088",
    val pendingRecordsCount: Int = 0,
    val lastTransferCount: Int = 0,
    val isMirroringActive: Boolean = false,
    val message: String = "Ready to connect over local encrypted Wi-Fi Direct"
)

/**
 * Interface representing the iQOO Office Kit / Local Doctor Workstation Handover.
 * Facilitates offline wireless transfer between field smartphones and PHC desktop systems.
 */
interface OfficeKitBridge {
    val state: Flow<OfficeKitState>
    suspend fun searchAndConnect()
    suspend fun transferPendingRecords(recordsCount: Int): Boolean
    fun toggleScreenMirroring(): Boolean
    fun disconnect()
}

class MockOfficeKitBridge : OfficeKitBridge {
    private val _state = MutableStateFlow(OfficeKitState())
    override val state = _state.asStateFlow()

    override suspend fun searchAndConnect() {
        _state.value = _state.value.copy(
            status = ConnectionStatus.SEARCHING_WORKSTATION,
            message = "Discovering Doctor's Workstation on local network..."
        )
        delay(1000)
        _state.value = _state.value.copy(
            status = ConnectionStatus.CONNECTED_DOCTOR_PC,
            message = "Doctor's workstation connected securely (TLS 1.3)"
        )
    }

    override suspend fun transferPendingRecords(recordsCount: Int): Boolean {
        if (_state.value.status != ConnectionStatus.CONNECTED_DOCTOR_PC) {
            searchAndConnect()
        }
        _state.value = _state.value.copy(
            status = ConnectionStatus.TRANSFERRING,
            message = "Syncing $recordsCount records with Primary Health Centre EHR..."
        )
        delay(1400)
        _state.value = _state.value.copy(
            status = ConnectionStatus.TRANSFER_COMPLETED,
            pendingRecordsCount = 0,
            lastTransferCount = recordsCount,
            message = "$recordsCount records transferred securely to Doctor's PC"
        )
        return true
    }

    override fun toggleScreenMirroring(): Boolean {
        val newStatus = !_state.value.isMirroringActive
        _state.value = _state.value.copy(
            isMirroringActive = newStatus,
            message = if (newStatus) "Screen mirroring active on Doctor's display" else "Mirroring paused"
        )
        return newStatus
    }

    override fun disconnect() {
        _state.value = OfficeKitState(
            status = ConnectionStatus.DISCONNECTED,
            message = "Disconnected from workstation"
        )
    }
}
