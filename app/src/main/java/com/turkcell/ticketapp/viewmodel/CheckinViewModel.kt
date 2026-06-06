package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.CheckinRepository
import com.turkcell.core.domain.CheckinResult
import com.turkcell.data.network.ApiException
import com.turkcell.ticketapp.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckinUiState(
    val isScanning: Boolean = true,
    val isProcessing: Boolean = false,
    val result: CheckinResult? = null,
    val errorMessage: String? = null
)

class CheckinViewModel(
    private val checkinRepository: CheckinRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckinUiState())
    val state: StateFlow<CheckinUiState> = _state.asStateFlow()

    fun onQrDetected(qrCode: String) {
        val current = _state.value
        if (current.isProcessing || !current.isScanning) return
        if (qrCode.isBlank()) return

        _state.update {
            it.copy(
                isScanning = false,
                isProcessing = true,
                errorMessage = null,
                result = null
            )
        }

        viewModelScope.launch {
            checkinRepository.scan(qrCode)
                .onSuccess { result ->
                    _state.update {
                        it.copy(isProcessing = false, result = result)
                    }
                }
                .onFailure { error ->
                    val apiCode = (error as? ApiException)?.code
                    val message = when (apiCode) {
                        409 -> "Bu bilet daha önce kullanılmış"
                        404 -> "Bilet bulunamadı / sahte QR"
                        403 -> "Bu etkinliğe atanmamışsınız"
                        else -> error.toUserMessage()
                    }
                    _state.update {
                        it.copy(isProcessing = false, errorMessage = message)
                    }
                }
        }
    }

    fun resetForNextScan() {
        _state.value = CheckinUiState(isScanning = true)
    }
}